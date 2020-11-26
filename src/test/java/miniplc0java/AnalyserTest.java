package miniplc0java;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class AnalyserTest {
    @Test
    public void lytTest() {
        int total = 50; // 总人数
        Random random = new Random();
        double sum = 0;
        int all = 0;
        for (int j = 0; j < 10000; j++) {
            sum = 0;
            for (int i = 0; i < total; i++) {
                int myNum = random.nextInt(100) + 1;    // 1-100
                sum += myNum;
            }
            int res = round(sum * 2 / 3);
            //System.out.println(res);
            all += res;
        }
        System.out.println(all / 10000);

    }

    public static int round(double s) {
        if (s - (int) s > 0.5)
            return (int) s + 1;
        return (int) s;
    }

}
