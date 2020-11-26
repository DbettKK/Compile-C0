package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tokenizer {

    private StringIter it;

    private List<String> keywords = new ArrayList<String>();

    {
        String[] s = {"fn", "let", "const", "as", "while", "if", "else", "return", "var", "print"};
        Collections.addAll(keywords, s);
    }

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return lexUInt();
        } else if (Character.isAlphabetic(peek) || peek == '_') {
            return lexIdentOrKeyword();
        } else if (peek == '\"'){
            return lexString();
        }else {
            return lexOperatorOrUnknown();
        }
    }
    private Token lexString() throws TokenizeError {
        StringBuilder sb = new StringBuilder();
        Pos startPos, endPos;
        startPos = it.nextPos();
        char next;
        sb.append(it.nextChar()); // 先把引号放入
        while (true) {
            next = it.peekChar();
            if (next == '\\') {
                it.nextChar();
                char tmp = it.peekChar();
                if (tmp == 'r' || tmp == 'n' || tmp == 't' || tmp == '\'' || tmp == '\"' || tmp == '\\') {
                    sb.append('\\').append(tmp);
                    it.nextChar();
                } else {
                    throw new TokenizeError(ErrorCode.InvalidEscapeCharacter, it.currentPos());
                }
            } else if(next == '\"') {
                sb.append(next);
                it.nextChar();
                break;
            } else if (next == '\0')  {
                throw new TokenizeError(ErrorCode.StringLiteralOpen, it.currentPos());
            } else {
                sb.append(next);
                it.nextChar();
            }
        }
        endPos = it.currentPos();
        return new Token(TokenType.STRING_LITERAL, sb.toString(), startPos, endPos);
    }

    private Token lexUInt() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
        StringBuilder sb = new StringBuilder();
        Pos startPos, endPos;
        startPos = it.nextPos();
        char next;
        while (true) {
            // it.peekChar();
            next = it.peekChar();
            if (Character.isDigit(next)) {
                sb.append(next);
                it.nextChar();
            }
            else break;
        }
        endPos = it.currentPos();   // 此时指针是指向该数字最后一位

        try {
            int num = Integer.parseInt(sb.toString());
            return new Token(TokenType.UINT_LITERAL, num, startPos, endPos);
        } catch (Exception e){
            throw new TokenizeError(ErrorCode.IntegerOverflow, startPos);
        }
    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        StringBuilder sb = new StringBuilder();
        Pos startPos, endPos;
        startPos = it.nextPos();
        char next;
        while (true) {
            // it.peekChar();
            next = it.peekChar();
            if (Character.isDigit(next) || Character.isAlphabetic(next) || next == '_') {
                sb.append(next);
                it.nextChar();
            }
            else break;
        }
        endPos = it.currentPos();   // 此时指针是指向该标识符最后一位

        String str = sb.toString();
        if (keywords.contains(str)){
            // 如果是关键字
            str = str.toUpperCase() + "_KW";
            return new Token(TokenType.valueOf(str), str, startPos, endPos);
        } else {
            return new Token(TokenType.IDENT, str, startPos, endPos);
        }
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.PLUS, '+', it.previousPos(), it.currentPos());

            case '-':
                if (it.peekChar() == '>') {
                    it.nextChar();
                    return new Token(TokenType.ARROW, "->", it.previousPos(), it.currentPos());
                } else
                    return new Token(TokenType.MINUS, '-', it.previousPos(), it.currentPos());

            case '*':
                return new Token(TokenType.MUL, '*', it.previousPos(), it.currentPos());

            case '/':
                return new Token(TokenType.DIV, '/', it.previousPos(), it.currentPos());

            case '=':
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.EQ, "==", it.previousPos(), it.currentPos());
                } else
                    return new Token(TokenType.ASSIGN, '=', it.previousPos(), it.currentPos());

            case '!':
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.NEQ, "!=", it.previousPos(), it.currentPos());
                } else
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());

            case '<':
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.LE, "<=", it.previousPos(), it.currentPos());
                } else
                    return new Token(TokenType.LT, '<', it.previousPos(), it.currentPos());

            case '>':
                if (it.peekChar() == '=') {
                    it.nextChar();
                    return new Token(TokenType.GE, ">=", it.previousPos(), it.currentPos());
                } else
                    return new Token(TokenType.GT, '>', it.previousPos(), it.currentPos());

            case '(':
                return new Token(TokenType.L_PAREN, '(', it.previousPos(), it.currentPos());

            case ')':
                return new Token(TokenType.R_PAREN, ')', it.previousPos(), it.currentPos());

            case '{':
                return new Token(TokenType.L_BRACE, '{', it.previousPos(), it.currentPos());

            case '}':
                return new Token(TokenType.R_BRACE, '}', it.previousPos(), it.currentPos());

            case ',':
                return new Token(TokenType.COMMA, ',', it.previousPos(), it.currentPos());

            case ':':
                return new Token(TokenType.COLON, ':', it.previousPos(), it.currentPos());

            case ';':
                return new Token(TokenType.SEMICOLON, ';', it.previousPos(), it.currentPos());

            default:
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
