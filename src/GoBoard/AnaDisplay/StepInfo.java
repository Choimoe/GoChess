package GoBoard.AnaDisplay;

import java.io.File;
import java.util.List;

public class StepInfo {
    public double winRate, scoreLead, winRateDrop, scoreDrop;

    public StepInfo(double winRate, double scoreLead, double winRateDrop, double scoreDrop) {
        this.winRate = winRate;
        this.scoreLead = scoreLead;
        this.winRateDrop = winRateDrop;
        this.scoreDrop = scoreDrop;
    }

    public static List<StepInfo> getDataFromAnalysedData(File T) {
        return null;
    }
}
