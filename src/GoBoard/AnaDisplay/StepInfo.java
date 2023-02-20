package GoBoard.AnaDisplay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class StepInfo {
    public double winRate, scoreLead, winRateDrop, scoreDrop;

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public void setScoreLead(double scoreLead) {
        this.scoreLead = scoreLead;
    }

    public void setWinRateDrop(double winRateDrop) {
        this.winRateDrop = winRateDrop;
    }

    public void setScoreDrop(double scoreDrop) {
        this.scoreDrop = scoreDrop;
    }

    public static List<StepInfo> getDataFromAnalysedData(File file) {
        List<StepInfo> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                if (line.startsWith("* Win rate:")) {
                    StepInfo step = new StepInfo();
                    String[] winRateParts = line.split(": ");

                    String first = winRateParts[1].substring(2);
                    int lenFirst = first.length();
                    step.setWinRate(Double.parseDouble(first.substring(0, lenFirst - 1)));

                    step.setScoreLead(Double.parseDouble(reader.readLine().split(": ")[1].substring(2)));

                    String third = reader.readLine().split(": ")[1].substring(3);
                    int lenThird = third.length();
                    step.setWinRateDrop(Double.parseDouble(third.substring(0, lenThird - 1)));
                    step.setScoreDrop(Double.parseDouble(reader.readLine().split(": ")[1].substring(3)));

                    results.add(step);
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}
