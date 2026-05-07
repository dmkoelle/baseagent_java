package org.baseagent.ui;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.sim.Simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This is intended to be a Toast that is displayed RIGHT NOW and pauses the
 * entire simulation for 'pauseDuration'
 */
public class PausingToast extends Toast {
    private boolean active = true;

    public PausingToast(int graphicX, int graphicY, int width, int height, String text) {
        super(graphicX, graphicY, width, height, text);
    }

    public void draw(GridCanvasContext gcc) {
        GraphicsContext gc = gcc.getGraphicsContext();

        // Draw the rounded rectangle
        double cornerRadius = 10; // Adjust as needed
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Fill and stroke the rounded rectangle
        gc.fillRoundRect(graphicX, graphicY, width, height, cornerRadius, cornerRadius);
        gc.strokeRoundRect(graphicX, graphicY, width, height, cornerRadius, cornerRadius);

        // Set up text properties
        Font font = new Font("Arial", 20);
        gc.setFont(font);
        gc.setFill(Color.BLACK);

        // Calculate text area with padding
        double padding = 10;
        double textAreaWidth = width - (2 * padding);
        double textAreaHeight = height - (2 * padding);
        double textX = graphicX + padding;
        double textY = graphicY + padding;

        // Split text by forward slashes first (manual line breaks)
        String[] manualLines = text.split("/");
        List<String> allLines = new ArrayList<>();

        // For each manual line, wrap it based on width
        for (String manualLine : manualLines) {
            List<String> wrappedLines = wrapText(manualLine.trim(), textAreaWidth, font);
            allLines.addAll(wrappedLines);
        }

        // Calculate line height
        double lineHeight = getTextHeight(font) + 2; // Add small spacing between lines

        // Draw each line of text
        double currentY = textY + getTextHeight(font); // Start at baseline
        for (String line : allLines) {
            // Check if we're still within the box bounds
            if (currentY > graphicY + height - padding) {
                break; // Stop drawing if we exceed the box height
            }

            gc.fillText(line, textX, currentY);
            currentY += lineHeight;
        }
    }

    // Helper method to wrap text based on available width
    private List<String> wrapText(String text, double maxWidth, Font font) {
        List<String> lines = new ArrayList<>();

        if (text.isEmpty()) {
            lines.add("");
            return lines;
        }

        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

            // Check if the test line fits within the available width
            if (getTextWidth(testLine, font) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                // Current line is full, add it to lines and start a new line
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Single word is too long, add it anyway to prevent infinite loop
                    lines.add(word);
                }
            }
        }

        // Add the last line if it's not empty
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    // Helper method to get text width
    private double getTextWidth(String text, Font font) {
        Text textNode = new Text(text);
        textNode.setFont(font);
        return textNode.getBoundsInLocal().getWidth();
    }

    // Helper method to get text height
    private double getTextHeight(Font font) {
        Text textNode = new Text("Ag"); // Use characters with ascenders and descenders
        textNode.setFont(font);
        return textNode.getBoundsInLocal().getHeight();
    }

    @Override
    public boolean isActive(Simulation simulation) {
        return this.active;
    }

    @Override
    public boolean readyToRemove(Simulation simulation) {
        return true;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
