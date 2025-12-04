package com.mangomusic.data;

import com.mangomusic.models.ReportResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MangoMusicDataManagerDao {
    private final DataManager dataManager;

    public MangoMusicDataManagerDao(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public List<ReportResult> getMostPlayedAlbumsByGenre() {
        List<ReportResult> results = new ArrayList<>();
        String query = "WITH ranked AS (" +
                "    SELECT " +
                "        ar.primary_genre, " +
                "        al.title, " +
                "        ar.name AS artist_name, " +
                "        COUNT(*) AS play_count, " +
                "        RANK() OVER (" +
                "            PARTITION BY ar.primary_genre " +
                "            ORDER BY COUNT(*) DESC " +
                "        ) AS genre_rank " +
                "    FROM album_plays ap " +
                "    JOIN albums al ON ap.album_id = al.album_id " +
                "    JOIN artists ar ON al.artist_id = ar.artist_id " +
                "    GROUP BY al.album_id, ar.primary_genre " +
                ")" +
                " SELECT * " +
                "FROM ranked " +
                "WHERE genre_rank <= 5 " +
                " ORDER BY primary_genre, genre_rank";

        try {
            Connection connection = dataManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            System.out.println(statement);
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    ReportResult result = new ReportResult();
                    result.addColumn("genre", rs.getString("primary_genre"));
                    result.addColumn("album_title", rs.getString("title"));
                    result.addColumn("artist_name", rs.getString("artist_name"));
                    result.addColumn("play_count", rs.getInt("play_count"));
                    result.addColumn("genre_rank", rs.getInt("genre_rank"));


                    results.add(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error running Most Played Albums By Genre report: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
}
