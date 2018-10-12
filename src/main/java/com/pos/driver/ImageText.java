package com.pos.driver;



       import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

       import java.awt.*;
       import java.awt.image.BufferedImage;
        import java.io.File;
       import java.io.FileOutputStream;
       import java.io.IOException;
        import javax.imageio.ImageIO;
       import javax.swing.text.StyleConstants;

public class ImageText {

    public static void main(String[] args) throws Exception
    {

        String text = "रि ता";

        if(true)
        {
            Font customFont =null;
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("007ARAP.TTF")).deriveFont(12f);

            BufferedImage bufferedImage = new BufferedImage(200, 60, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(Color.PINK);
            graphics.setFont(new Font("ARAP007", Font.BOLD, 24));
            //String text1=new String(MimeUtility.encodeText("रि ता ", "utf-8","B"));
            graphics.drawString("ld", 20, 40);
            ImageIO.write(bufferedImage, "png", new FileOutputStream(new File("rest.png")));


            return;
        }

        /*
           Because font metrics is based on a graphics context, we need to create
           a small, temporary image so we can ascertain the width and height
           of the final image
         */
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial",Font.BOLD, 48);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File("Text.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}