package com.Tetris;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class MainWindow extends JPanel
{
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        drawLayout(g);
    }

    private void drawLayout(Graphics g)
    {
        int center_x = getWidth()/2;
        int center_y = getHeight()/2;
        int arcs = (int)(getHeight() * 0.01);

        Graphics2D g2d = (Graphics2D) g;

        int matrix_height = (int)(0.8 * getHeight());
        int matrix_width = (int)(0.6 * matrix_height);

        int matrix_block_size = matrix_width / 12;
        int block_pos_x = (center_x - matrix_width / 2);
        int block_pos_y = (center_y - matrix_height / 2);

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.DARK_GRAY);
        //g2d.drawRoundRect((int)(center_x - matrix_width / 2), (int)(center_y - matrix_height / 2), matrix_width, matrix_height, arcs, arcs);


        for (int i = 0; i < 65; i++)
        {
            g2d.drawRoundRect(block_pos_x, block_pos_y, matrix_block_size, matrix_block_size, arcs, arcs);

            if(i > 0 && i < 12)
                block_pos_x += matrix_block_size;
            else if (i >= 12 && i < 32)
                block_pos_y += matrix_block_size;
            else if (i >= 34 && i < 45)
                block_pos_x -= matrix_block_size;
            else if (i >= 45)
                block_pos_y -= matrix_block_size;

            g2d.setColor(Color.getHSBColor((float)i / 64, 1, 1));
        }

    }


}
