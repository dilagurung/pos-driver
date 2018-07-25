package com.pos.driver;


import org.json.simple.JSONArray;
    import org.json.simple.JSONObject;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import java.util.HashMap;
import java.util.Map;

public class PrinterOptions {


    String commandSet = "";


    public String initialize() {
        final byte[] Init = {27, 64};
        commandSet += new String(Init);
        return new String(Init);
    }

    public String chooseFont(int Options) {
        String s = "";
        final byte[] ChooseFontA = {27, 77, 0};
        final byte[] ChooseFontB = {27, 77, 1};
        final byte[] ChooseFontC = {27, 77, 48};
        final byte[] ChooseFontD = {27, 77, 49};

        switch(Options) {
            case 1:
                s = new String(ChooseFontA);
                break;

            case 2:
                s = new String(ChooseFontB);
                break;

            case 3:
                s = new String(ChooseFontC);
                break;

            case 4:
                s = new String(ChooseFontD);
                break;

            default:
                s = new String(ChooseFontB);
        }
        commandSet += s;
        return new String(s);
    }

    public String feedBack(byte lines) {
        final byte[] Feed = {27,101,lines};
        String s = new String(Feed);
        commandSet += s;
        return s;
    }

    public String feed(byte lines) {
        final byte[] Feed = {27,100,lines};
        String s = new String(Feed);
        commandSet += s;
        return s;
    }

    public String alignLeft() {
        final byte[] AlignLeft = {27, 97,48};
        String s = new String(AlignLeft);
        commandSet += s;
        return s;
    }

    public String alignCenter() {
        final byte[] AlignCenter = {27, 97,49};
        String s = new String(AlignCenter);
        commandSet += s;
        return s;
    }

    public String alignRight() {
        final byte[] AlignRight = {27, 97,50};
        String s = new String(AlignRight);
        commandSet += s;
        return s;
    }

    public String newLine() {
        final  byte[] LF = {10};
        String s = new String(LF);
        commandSet += s;
        return s;
    }

    public String reverseColorMode(boolean enabled) {
        final byte[] ReverseModeColorOn = {29, 66, 1};
        final byte[] ReverseModeColorOff = {29, 66, 0};

        String s = "";
        if(enabled)
            s = new String(ReverseModeColorOn);
        else
            s = new String(ReverseModeColorOff);

        commandSet += s;
        return s;
    }

    public String doubleStrik(boolean enabled) {
        final byte[] DoubleStrikeModeOn = {27, 71, 1};
        final byte[] DoubleStrikeModeOff = {27, 71, 0};

        String s="";
        if(enabled)
            s = new String(DoubleStrikeModeOn);
        else
            s = new String(DoubleStrikeModeOff);

        commandSet += s;
        return s;
    }

    public String doubleHeight(boolean enabled) {
        final byte[] DoubleHeight = {27, 33, 17};
        final byte[] UnDoubleHeight={27, 33, 0};

        String s = "";
        if(enabled)
            s = new String(DoubleHeight);
        else
            s = new String(UnDoubleHeight);

        commandSet += s;
        return s;
    }

    public String emphasized(boolean enabled) {
        final byte[] EmphasizedOff={27 ,0};
        final byte[] EmphasizedOn={27 ,1};

        String s="";
        if(enabled)
            s = new String(EmphasizedOn);
        else
            s = new String(EmphasizedOff);

        commandSet += s;
        return s;
    }

    public String underLine(int Options) {
        final byte[] UnderLine2Dot = {27, 45, 50};
        final byte[] UnderLine1Dot = {27, 45, 49};
        final byte[] NoUnderLine = {27, 45, 48};

        String s = "";
        switch(Options) {
            case 0:
                s = new String(NoUnderLine);
                break;

            case 1:
                s = new String(UnderLine1Dot);
                break;

            default:
                s = new String(UnderLine2Dot);
        }
        commandSet += s;
        return new String(s);
    }

    public String color(int Options) {
        final byte[] ColorRed = {27, 114, 49};
        final byte[] ColorBlack = {27, 114, 48};

        String s = "";
        switch(Options) {
            case 0:
                s = new String(ColorBlack);
                break;

            case 1:
                s = new String(ColorRed);
                break;

            default:
                s = new String(ColorBlack);
        }
        commandSet += s;
        return s;
    }


    public String finit() {

     /*  final byte[] FeedAndCut = {29, 'V', 66, 0};

        String s = new String(FeedAndCut);

        final byte[] DrawerKick={27,70,0,60,120};
        s += new String(DrawerKick);

        commandSet+=s;*/
        return null;
    }

    public String addLineSeperator() {
        String lineSpace = "----------------------------------------------------";
        commandSet += lineSpace;
        return lineSpace;
    }

    public void resetAll() {
        commandSet = "";
    }

    public void setText(String s) {
        commandSet+=s;
    }

    public String finalCommandSet() {
        return commandSet;
    }



    private static boolean feedPrinter(byte[] b) {
        try {


            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

            PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                    flavor, pras);
            System.out.println("Http driver "+HttpDriver.printerName);
            PrintService service = findPrintServices(HttpDriver.printerName, printService);

            DocPrintJob job = service.createPrintJob();

           // AttributeSet attrSet = new HashPrintServiceAttributeSet(new PrinterName("dd", null)); //EPSON TM-U220 ReceiptE4
           // DocPrintJob job = PrintServiceLookup.lookupPrintServices(null, attrSet)[0].createPrintJob();
            //PrintServiceLookup.lookupDefaultPrintService().createPrintJob();
            byte[] bytes;

            // important for umlaut chars

            //bytes = "redddd".getBytes("CP437");
            Doc doc = new SimpleDoc(b, flavor, null);
            PrintJobWatcher pjDone = new PrintJobWatcher(job);

            job.print(doc, null);
            pjDone.waitForDone();
            System.out.println("Done !");
        } catch (PrintException pex) {
            System.out.println("Printer Error " + pex.getMessage());
            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static PrintService findPrintServices(String printerName,
                                          PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName))
            {
                System.out.println("printer found");
                return service;
            }
        }
        System.out.println("printer not found");
        return null;
    }


   private String  createSpace(int numberOfSpace)
   {
       String _space= " ";
       for(int i=1;i<numberOfSpace;i++)
       {
           _space+=_space;
       }
       return _space;
   }


    protected boolean printReceipt(String id, JSONObject organization, String terminal, String user, String total, String customer, JSONArray items,String date,String tax)
    {

        System.out.println("reaching printer receipt");
        PrinterOptions p = new PrinterOptions();
        p.resetAll();
        p.initialize();
        p.feedBack((byte) 2);
        //p.color(0);
        p.alignCenter();
        p.setText(organization.get("name").toString());
        p.newLine();
        p.setText("CIF : "+organization.get("pan").toString());
        p.newLine();

        p.setText(organization.get("addressOne").toString());
        //p.setText("Address 1");
        p.newLine();
        p.setText(organization.get("addressTwo").toString());
        //p.setText("Address 2");
        p.newLine();

        p.setText("Tel : "+organization.get("phone").toString());
        p.newLine();
        p.addLineSeperator();
        p.newLine();
        p.alignLeft();
        p.setText("Receipt : "+id);
        p.newLine();

        p.setText("Date : "+date); //createdOn
        p.newLine();
        p.setText("Terminal : "+terminal);
        p.newLine();

        p.setText("Served by : "+user);
        p.newLine();

        p.addLineSeperator();
        p.newLine();




/*
        p.alignLeft();
        p.setText("POD No \t: 2001 \tTable \t: E511");
        p.newLine();

        p.setText("Res Date \t: " + "01/01/1801 22:59");

        p.newLine();
        p.setText("Session \t: Evening Session");
        p.newLine();
        p.setText("Staff \t: Bum Dale");
        p.newLine();
        p.addLineSeperator();*/
        p.newLine();
        p.alignCenter();
        p.setText(" -  Items - ");
        p.newLine();
        p.alignLeft();
        p.addLineSeperator();

        p.newLine();

        p.setText("Item\t\t\tPrice\tQty\tValue");
        p.newLine();
        p.addLineSeperator();
        p.newLine();
        //severity high : either  restrict items  length or split it into next line
       int itemLength=22;
       int priceLength=7;
       int qtyLength=4;
       int valueLength=7;
        String space="";
        System.out.println("reaching printer receipt 2");
        for (int i = 0; i < items.size(); i++)
        {
            Map<String, Object> item = new HashMap<>();
            item=(HashMap<String,Object>)items.get(i);
            String productDescription=item.get("productDescription")+"";
            int pdLength=productDescription.length();


            String _productDescription="";
            if(pdLength<23)
            {
                 space = createSpace(itemLength - pdLength);
                _productDescription=productDescription+space;
            }
            _productDescription = _productDescription.substring(0, itemLength);
            p.setText(_productDescription);
            String price = item.get("price")+"";
            space=createSpace(priceLength-price.length());
            String _price=price+space;
            p.setText(_price);

            String quantity=item.get("quantity")+"";
            space=createSpace(qtyLength-quantity.length());
            String _quantity=quantity+space;
            p.setText(_quantity);

            String value=item.get("total")+"";
            space=createSpace(valueLength-value.length());
            String _value=value+space;
            p.setText(_value);


            p.newLine();
        }


        /*p.setText("Aliens Everywhere" + "\t" + "Rats" + "\t" + "500"+ "\t" + "500");
        p.newLine();
  */




        p.addLineSeperator();
        p.newLine();
        p.newLine();
        p.setText("Items count : "+items.size());
        p.newLine();
        p.newLine();
        //p.chooseFont(1);
        p.doubleHeight(true);
        p.doubleStrik(true);
        p.setText("Total");
        p.setText("\t\t\t\t        "+total+ " "+0xA4); //severity high ...make it dynamic
        p.newLine();
        p.newLine();
        p.doubleHeight(false);
        p.setText("Nett of Tax");
        p.setText("\t\t\t      "+total+" Euro");
        p.newLine();
        p.newLine();
        p.doubleStrik(false);
        p.setText("Taxes\t\t\t     "+tax+ "");
        p.alignCenter();
        p.newLine();
        p.setText("Thank you");
        p.feed((byte) 1);
        p.finit();
        System.out.println("reaching printer receipt 3");
        feedPrinter(p.finalCommandSet().getBytes());
       return false;
    }
    }

class PrintJobWatcher {
    boolean done = false;

    PrintJobWatcher(DocPrintJob job) {
        job.addPrintJobListener(new PrintJobAdapter() {
            public void printJobCanceled(PrintJobEvent pje) {
                synchronized (PrintJobWatcher.this) {
                    done = true;
                    PrintJobWatcher.this.notify();
                }
            }

            public void printJobCompleted(PrintJobEvent pje) {
                synchronized (PrintJobWatcher.this) {
                    done = true;
                    PrintJobWatcher.this.notify();
                }
            }

            public void printJobFailed(PrintJobEvent pje) {
                synchronized (PrintJobWatcher.this) {
                    done = true;
                    PrintJobWatcher.this.notify();
                }
            }

            public void printJobNoMoreEvents(PrintJobEvent pje) {
                synchronized (PrintJobWatcher.this) {
                    done = true;
                    PrintJobWatcher.this.notify();
                }
            }
        });
    }

    public synchronized void waitForDone() {
        try {
            while (!done) {
                wait();
            }
        } catch (InterruptedException e) {
        }

        
        
}}