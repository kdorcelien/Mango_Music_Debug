package com.mangomusic.ui;

import com.mangomusic.data.MangoMusicDataManagerDao;
import com.mangomusic.data.ReportsDao;
import com.mangomusic.models.ReportResult;
import com.mangomusic.util.ConsoleColors;
import com.mangomusic.util.InputValidator;

import java.util.List;

public class SpecialReportsScreen {

    private final ReportsDao reportsDao;
    private final MangoMusicDataManagerDao mangoMusicDataManagerDao;

    public SpecialReportsScreen(ReportsDao reportsDao, MangoMusicDataManagerDao mangoMusicDataManagerDao) {
        this.reportsDao = reportsDao;
        this.mangoMusicDataManagerDao = mangoMusicDataManagerDao;
    }

    public void display() {
        boolean running = true;

        while (running) {
            InputValidator.clearScreen();
            displayMenu();

            int choice = InputValidator.getIntInRange("Select an option: ", 0, 2);

            switch (choice) {
                case 1:
                    showMangoMusicMapped();
                    break;
                case 2:
                    showMostPlayedAlbumsByGenre();
                    break;
                case 3:
                    //@TODO - Create report
//                    showUserDiversityScore();
                    break;
                case 4:
                    //@TODO - Create report
//                    showPeakListeningHours();
                    break;
                case 0:
                    running = false;
                    break;
            }
        }
    }

    private void displayMenu() {
        ConsoleColors.printHeader("SPECIAL REPORTS");

        System.out.println("\nPERSONALIZED ANALYTICS:");
        System.out.println("1. MangoMusic Mapped (Year in Review)");
        System.out.println("2. Most Played Albums by Genre");
        System.out.println("3. User Listening Diversity Score");
        System.out.println("4. Peak Listening Hours Analysis");

        System.out.println("\n0. Back to main menu");
        System.out.println();
    }

    private void showMangoMusicMapped() {
        InputValidator.clearScreen();
        ConsoleColors.printHeader("🎵 MANGOMUSIC MAPPED 🎵");
        System.out.println("Your personalized year in review\n");

        int userId = InputValidator.getIntInRange("Enter user ID: ", 1, Integer.MAX_VALUE);

        ReportResult mapped = reportsDao.getMangoMusicMapped(userId);

        int year = mapped.getInt("year");

        if (mapped.getInt("total_plays") == 0) {
            ConsoleColors.printWarning("No listening data found for user ID " + userId + " in " + year + ".");
        } else {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("YOUR " + year + " LISTENING STORY");
            System.out.println("=".repeat(70));

            System.out.println("\n🎧 LISTENING STATS:");
            System.out.println("   Total Plays: " + mapped.getInt("total_plays"));
            System.out.println("   Albums Explored: " + mapped.getInt("unique_albums"));
            System.out.println("   Artists Discovered: " + mapped.getInt("unique_artists"));
            System.out.println("   Completion Rate: " +
                    String.format("%.1f%%", (mapped.getInt("completed_plays") * 100.0 / mapped.getInt("total_plays"))));

            System.out.println("\n⭐ YOUR TOP PICKS:");
            System.out.println("   #1 Artist: " + mapped.getString("top_artist") +
                    " (" + mapped.getInt("top_artist_plays") + " plays)");
            System.out.println("   Favorite Genre: " + mapped.getString("top_genre"));
            System.out.println("   Most Active Month: " + mapped.getString("top_month") +
                    " (" + mapped.getInt("top_month_plays") + " plays)");

            System.out.println("\n🔥 FUN FACTS:");
            System.out.println("   Longest Listening Streak: " + mapped.getInt("longest_streak") + " days");
            System.out.println("   Listener Personality: " + mapped.getString("listener_personality"));

            System.out.println("\n" + "=".repeat(70));
            System.out.println("Thanks for making " + year + " a year full of music! 🎶");
            System.out.println("=".repeat(70));
        }

        InputValidator.pressEnterToContinue();
    }

    private void showMostPlayedAlbumsByGenre() {
        InputValidator.clearScreen();
        ConsoleColors.printHeader("🎵 MOST PLAYED ALBUMS BY GENRE 🎵");
        System.out.println("Top 5 albums ranked within each genre\n");

        List<ReportResult> results = mangoMusicDataManagerDao.getMostPlayedAlbumsByGenre();

        if (results.isEmpty()) {
            ConsoleColors.printWarning("No data available for this report.");
        } else {
            System.out.printf("%-10s %-50s %22s %16s %15s %n", "genre", "album_title", "artist_name", "play_count", "genre_rank");
            System.out.println("-".repeat(115));

            int displayCount = Math.min(results.size(), 50);
            for (int i = 0; i < displayCount; i++) {
                ReportResult result = results.get(i);
                System.out.printf("%-10s %-50s %22s %16s %15s %n",
                        result.getString("genre"),
                        result.getString("album_title"),
                        result.getString("artist_name"),
                        result.getInt("play_count"),
                        result.getInt("genre_rank"));
            }

            if (results.size() > 50) {
                System.out.println("\n... and " + (results.size() - 50) + " more rows");
            }

            System.out.println("\n");
            InputValidator.pressEnterToContinue();
        }

    }
}