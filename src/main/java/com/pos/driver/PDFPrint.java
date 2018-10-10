package com.pos.driver;
/**
 * Created by oa on 3/19/2018.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import com.sun.image.codec.jpeg.JPEGCodec;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;


public class PDFPrint implements Printable
{
    static int   currentHeightCursor=0;
    int top=560;
    int totalCharSupported =26;
    int spaceChar=0;
    int vMargin=25;
    int vSubMargin=18;
    int vInnerSubMargin=11;
    int horizonLength=160;
    String barCodeName="barCode.png";
    String barCodePDFName="barCode.pdf";

    static String logoName="logo.jpeg";
    int hMargin=10;
    int imageHeight=80;
    int currencyTextShift=0;
    static String printerName="POS-80C";
    public List<String> getPrinters(){

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);

        List<String> printerList = new ArrayList<String>();
        for(PrintService printerService: printServices){
            printerList.add( printerService.getName());
        }

        return printerList;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page)
            throws PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

		/*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
		/* Now we perform our rendering */

        g.setFont(new Font("Roman", 0, 8));
        g.drawString("Hello world !", 0, 10);

        return PAGE_EXISTS;
    }


    public static void main(String args[]) throws Exception
    {
        new PDFPrint().prinBanner();

    }

/*
    public static void main(String args[]) throws Exception
    {
 *//*       new PDFPrint().printBarCode(printerName, "Channel Head", "125444", 222.55);
        URL url = new URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXzqSvoANigosyTpH-A2kNJ8y9-DSpzcnYmNHQGHHEvcQzwQynIQ");
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();

        FileOutputStream fos = new FileOutputStream(logoName);
        fos.write(response);
        fos.close();
        BufferedImage originalImgage = ImageIO.read(new File("logo.png"));
        ImageIO.write(Scalr.resize(originalImgage, Method.ULTRA_QUALITY, 200), "png",new File(logoName) );
 *//*    //   new PDFPrint().printCashReport(logoName, "Samuel ", 22.25, 11221.55, 112211.55, 48888.5, 22.5, 48.5, 43, "2017-12-12", "2017-12-12", "TERMINAL A");


    }*/


    public static String getSpace(int spaceChar)
    {

        String space="";
        for(int i=0;i<spaceChar;i++)
        {
            space+=" ";
        }

        return space;

    }




    public void addContent(PDPageContentStream contentStream, PDFont pdFont,String text,int size,int vMargin,int _hMargin,String space) throws Exception
    {
        contentStream.beginText();
        contentStream.setFont(pdFont, size);
        currentHeightCursor=currentHeightCursor-vMargin;
        contentStream.newLineAtOffset(_hMargin, currentHeightCursor);
        contentStream.showText(space+text+space);
        contentStream.endText();
        System.out.println(currentHeightCursor);

    }


    public String getLocaleCurrency(Double value)
    {
        currencyTextShift=(NumberFormat.getCurrencyInstance(new Locale("es", "ES")).format(value).trim().length());

        if(currencyTextShift==8)
        {
            currencyTextShift=6;
        }

        else if(currencyTextShift>7)
        {
            //	6 15 21 27   12+15, 11+10, 10+5,9
            currencyTextShift=6*(currencyTextShift-8)+3;
        }
        else
        {
            currencyTextShift=currencyTextShift-7;
        }


        return NumberFormat.getCurrencyInstance(new Locale("es", "ES")).format(value);
    }






    public   boolean prinBanner() throws Exception
    {
       /* InputStream is = new BufferedInputStream(
                new FileInputStream(new File("logo.png")));
        BufferedImage image = ImageIO.read(is);



        Resizer resizer = DefaultResizerFactory.getInstance().getResizer(
                new Dimension(image.getWidth(), image.getHeight()),
                new Dimension(1000, 1200));
        BufferedImage scaledImage = new FixedSizeThumbnailMaker(
                1000, 1200, false, true).resizer(resizer).make(image);

*/


        /*

        com.sun.image.codec.jpeg.JPEGImageDecoder jpegDecoder =  JPEGCodec.createJPEGDecoder (new FileInputStream(new File("logo1.jpeg")));

        BufferedImage image = jpegDecoder.decodeAsBufferedImage();
*/

        //ImageIO.write(Scalr.resize(image, Method.AUTOMATIC, 1000), "png",new File("logo1.png") );
      //  BufferedImage scaledImage = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, 1600, 1000);
        //ImageIO.write(scaledImage,"png",new File("abc.png"));
        int orgLength="ddddddddd".length();
        if(orgLength<totalCharSupported)
        {
            spaceChar=(totalCharSupported-orgLength)/2;
        }
        String space=getSpace(spaceChar*2);

        //Loading an existing document
        PDDocument document = new PDDocument();
        //Retrieving the pages of the document
        PDPage page = new PDPage(PDRectangle.A4);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
 //       PDPageContentStream contentStream = new PDPageContentStream(document, page, true, false);
/*
        contentStream.beginText();
        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_BOLD, 20);
        contentStream.newLineAtOffset(0,0);
        String text = "Close Cash Report";
     //   contentStream.showText(text);
       contentStream.endText();*/
        currentHeightCursor=1;
        String originalFileName="coke-original.jpg";
        PDImageXObject pdImage = PDImageXObject.createFromFile(originalFileName,document);
        document.addPage(page);
        float heightCursor=654;
        Dimension scaledDim = getScaledDimension(new Dimension(pdImage.getWidth(),  pdImage.getHeight()), new Dimension(180, 100));
        contentStream.drawXObject(pdImage, 0, 654, 595, 188.97f);
        contentStream.drawXObject(pdImage, 0, heightCursor-=200, 595, 188.97f);
        contentStream.drawXObject(pdImage, 0, heightCursor-=200, 595, 188.97f);
        contentStream.drawXObject(pdImage, 0, heightCursor-=200, 595, 188.97f);

        //contentStream.drawi
        //contentStream.drawImage(pdImage, hMargin, currentHeightCursor);
        /*addContent(contentStream, PDType1Font.TIMES_ROMAN, "ddddddddd", 15,vMargin,0,space);
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Payment Reports", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Amount", 12,vSubMargin-vSubMargin,horizonLength-5,"");
        */
        contentStream.beginText();
        //Setting the font to the Content stream

        contentStream.setNonStrokingColor(Color.white);
        contentStream.setStrokingColor(Color.black);
        PDType0Font font = PDType0Font.load(document, new File("007ARAP.TTF"));
        contentStream.setFont(font, 22.5f);
        contentStream.newLineAtOffset(490,top+205);
        String text = "reredfdf";
        contentStream.showText(text);
        //contentStream.drawString(text);
        //byte[] commands = text.getBytes();
       // commands[1] = (byte) 128;
        //contentStream.appendRawCommands(commands);
        contentStream.endText();

        contentStream.close();

        //Saving the document
        String fileName="new2.pdf";
        document.save(fileName);
        //Closing the document
        document.close();

       // print(fileName);
  return false;
    }

    private static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // check if width needs to be scaled
        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        // then check height still needs to be scaled
        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }












    //fix image size
    public   boolean printCashReport(String imageName, String org, Double cash,Double cheque,Double subTotal, Double total,Double tax, Double taxExempt, long numberOfPayments,String startDate,String endDate,String terminal,String receiptID) throws Exception
    {
        int orgLength=org.length();
        if(orgLength<totalCharSupported)
        {
            spaceChar=(totalCharSupported-orgLength)/2;
        }
        String space=getSpace(spaceChar*2);

        //Loading an existing document
        PDDocument document = new PDDocument();
        //Retrieving the pages of the document
        PDPage page = new PDPage(PDRectangle.A5);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_BOLD, 20);
        //Setting the position for the line
        contentStream.newLineAtOffset(25,top);
        String text = "Close Cash Report";
            /*//Adding text in the form of string
            contentStream.showText(NumberFormat.getCurrencyInstance(new Locale("es", "ES")).format(22.22));
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);*/
        contentStream.showText(text);
        //Ending the content stream
        contentStream.endText();
        currentHeightCursor=top-imageHeight;
        PDImageXObject pdImage = PDImageXObject.createFromFile(logoName,document);
        document.addPage(page);
        contentStream.drawImage(pdImage, hMargin, currentHeightCursor);
        System.out.println(currentHeightCursor);
        addContent(contentStream, PDType1Font.TIMES_ROMAN, org, 15,vMargin,0,space);
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Payment Reports", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Amount", 12,vSubMargin-vSubMargin,horizonLength-5,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "-------------------------------------------------", 12,vInnerSubMargin,hMargin-5,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "Cash : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, getLocaleCurrency(cash), 12,vSubMargin-vSubMargin,horizonLength-currencyTextShift	,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "Cheque : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, getLocaleCurrency(cheque), 12,vSubMargin-vSubMargin,horizonLength-currencyTextShift,"");

        addContent(contentStream, PDType1Font.TIMES_ROMAN, "-------------------------------------------------", 12,vSubMargin,hMargin-5,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Total : ", 12,vInnerSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, getLocaleCurrency(total), 12,vInnerSubMargin-vInnerSubMargin,horizonLength-currencyTextShift,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "Number of Payments : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, numberOfPayments+"", 12,vSubMargin-vSubMargin,horizonLength-8,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Tax Analysis : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Amount", 12,vSubMargin-vSubMargin,horizonLength-5,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "-------------------------------------------------", 12,vInnerSubMargin,hMargin-5,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "Tax Exempt : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, getLocaleCurrency(taxExempt), 12,vSubMargin-vSubMargin,horizonLength-currencyTextShift,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "-------------------------------------------------", 12,vSubMargin,hMargin-5,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "Receipt/SID : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, ""+receiptID, 12,vSubMargin-vSubMargin,horizonLength-42,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Subtotal : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, getLocaleCurrency(subTotal), 12,vSubMargin-vSubMargin,horizonLength-currencyTextShift,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Taxes : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, getLocaleCurrency(tax), 12,vSubMargin-vSubMargin,horizonLength-currencyTextShift,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, "Total : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_BOLD, getLocaleCurrency(total), 12,vSubMargin-vSubMargin,horizonLength-currencyTextShift,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "-------------------------------------------------", 12,vSubMargin,hMargin-5,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "Terminal/IP : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, terminal, 12,vSubMargin-vSubMargin,horizonLength-36,"");

        addContent(contentStream, PDType1Font.TIMES_ROMAN, "Start Date : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, startDate, 12,vSubMargin-vSubMargin,horizonLength-20,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, "End Date : ", 12,vSubMargin,hMargin,"");
        addContent(contentStream, PDType1Font.TIMES_ROMAN, endDate, 12,vSubMargin-vSubMargin,horizonLength-20,"");
        //Closing the content stream
        contentStream.close();

        //Saving the document
        String fileName="new.pdf";
        document.save(fileName);
        //Closing the document
        document.close();

        print(fileName);

        return false;


        //print some stuff
//            printerService.printString("POS-80C", "vegetable  ", "1457895222",22.0);*/

/*            byte[] cutP = new byte[] { 0x1d, 'V', 1 };

            printerService.printBytes("POS-80C", cutP);*/
    }

    public void printBarCode(String printerName, String productName, String productId, Double price )
    {
        Barcode b=null;
        File f = null;
        try {
            f=new File(barCodeName);
            b = BarcodeFactory.createCode128(productId);
            b.setResolution(140);
            b.setBarHeight(80);
            b.setBarWidth(1);
            b.setDrawingText(false);
            BufferedImage bufferedImage = new BufferedImage(200, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setBackground(Color.WHITE);
            g2d.setColor(Color.BLACK);
            BarcodeImageHandler.savePNG(b, f);
            //b.setToolTipText("hello");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //add image to pdf;

        try{
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A5);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDImageXObject pdImage = PDImageXObject.createFromFile(barCodeName,document);
            document.addPage(page);
            contentStream.drawImage(pdImage, 45, 500);


            System.err.println("bar code length "+(productName.length()/2));

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(95-3*(productName.length()), 585);
            contentStream.showText(productName);
            contentStream.endText();


            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(95-3*(NumberFormat.getCurrencyInstance(new Locale("es", "ES")).format(price).length()), 488);
            contentStream.showText(NumberFormat.getCurrencyInstance(new Locale("es", "ES")).format(price));
            contentStream.endText();

            contentStream.close();

            document.save(barCodePDFName);
            document.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

         print(barCodePDFName);
        //printer code





    }


public void print(String fileName)
{


    PDDocument document=null;
    try {
        document = PDDocument.load(new File(fileName));
    } catch (InvalidPasswordException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    PrintService myPrintService = findPrintService(printerName);

    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPageable(new PDFPageable(document));
    try {
        job.setPrintService(myPrintService);
        job.print();

    } catch (PrinterException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }


}


    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }








}
//http://book2s.com/java/api/org/apache/pdfbox/pdmodel/pddocument/addpage-1.html