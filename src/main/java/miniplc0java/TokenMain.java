package miniplc0java;

import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TokenMain {
    private static Tokenizer tokenize(StringIter iter) {
        return new Tokenizer(iter);
    }

    public static void main(String[] args) throws FileNotFoundException {
        String path = "D:\\大学\\大三上\\编译\\作业\\miniplc0-java\\build\\libs\\in.txt";
        File f = new File(path);
        Scanner scanner = new Scanner(new FileReader(f));
        StringIter iter = new StringIter(scanner);
        var tokenizer = tokenize(iter);
        List<Token> tokens = new ArrayList<Token>();
        try {
            while (true) {
                Token token = tokenizer.nextToken();
                if (token.getTokenType().equals(TokenType.EOF)) {
                    break;
                }
                tokens.add(token);
            }
        } catch (Exception e) {
            // 遇到错误不输出，直接退出
            e.printStackTrace();
            System.exit(0);
        }
        for (Token token : tokens) {
            System.out.println(token.toString());
        }
    }
}
