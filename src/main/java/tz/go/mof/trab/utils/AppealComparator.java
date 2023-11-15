package tz.go.mof.trab.utils;

import tz.go.mof.trab.models.Appeals;

import java.util.Comparator;

public class AppealComparator implements Comparator<Appeals> {
    @Override
    public int compare(Appeals appeal1, Appeals appeal2) {
        try {
            String[] parts1 = appeal1.getAppealNo().split("/");
            String[] parts2 = appeal2.getAppealNo().split("/");

            int numericPart1 = Integer.parseInt(parts1[0].split("\\.")[1]);
            int numericPart2 = Integer.parseInt(parts2[0].split("\\.")[1]);
            int yearPart1 = Integer.parseInt(parts1[1]);
            int yearPart2 = Integer.parseInt(parts2[1]);

            if (numericPart1 != numericPart2) {
                return Integer.compare(numericPart1, numericPart2);
            } else if (yearPart1 != yearPart2) {
                return Integer.compare(yearPart1, yearPart2);
            } else {
                return appeal1.getAppealNo().compareTo(appeal2.getAppealNo());
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            // Handle cases where appeal number doesn't follow the standard format
            return 1; // Move them to the end of the list
        }
    }
}