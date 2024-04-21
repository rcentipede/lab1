package org.example;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String url = "https://store.steampowered.com/search/?filter=topsellers&filter=recent";

        // Specify the directory for the output Excel file
        String outputDirectory = "C:\\Users\\vital\\Downloads\\file\\";

        // Create a new Excel workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Steam Games");

        try {
            Document doc = Jsoup.connect(url).get();
            Elements games = doc.select(".search_result_row");

            // Create a list to store game data
            List<Game> gameList = games.stream()
                    .map(game -> new Game(
                            game.select(".title").text(),
                            game.select(".search_price").text(),
                            game.select(".search_discount_pct").text(),
                            game.select(".search_review_summary").attr("data-tooltip-html")
                    ))
                    .collect(Collectors.toList());

            // Sort games by rating in descending order
            gameList.sort(Comparator.comparing(Game::getRating).reversed());

            // Select top 10 games with highest ratings
            List<Game> topGames = gameList.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            // Write top 10 games to Excel
            int rowNum = 0;
            for (Game game : topGames) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(game.getTitle());
                row.createCell(1).setCellValue(game.getPrice());
                row.createCell(2).setCellValue(game.getDiscount());
                row.createCell(3).setCellValue(game.getRating());
            }

            // Save the workbook to an Excel file
            try (FileOutputStream fileOut = new FileOutputStream(outputDirectory + "top_10_games.xlsx")) {
                workbook.write(fileOut);
            }

            System.out.println("Excel file created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class Game {
        private String title;
        private String price;
        private String discount;
        private String rating;

        public Game(String title, String price, String discount, String rating) {
            this.title = title;
            this.price = price;
            this.discount = discount;
            this.rating = rating;
        }

        public String getTitle() {
            return title;
        }

        public String getPrice() {
            return price;
        }

        public String getDiscount() {
            return discount;
        }

        public String getRating() {
            return rating;
        }
    }
}