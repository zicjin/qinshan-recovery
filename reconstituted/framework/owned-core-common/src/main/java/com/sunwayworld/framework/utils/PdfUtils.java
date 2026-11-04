package com.sunwayworld.framework.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class PdfUtils {
   public static void mergePdfFiles(Path destPath, Path... pdfPaths) {
      if (!ArrayUtils.isEmpty(pdfPaths)) {
         String[] absolutePdfPaths = Arrays.stream(pdfPaths).map(Path::toString).toArray(String[]::new);
         mergePdfFiles(destPath.toString(), absolutePdfPaths);
      }
   }

   public static void mergePdfFiles(String targetFilePath, String... sourcePdfPaths) {
      if (!ArrayUtils.isEmpty(sourcePdfPaths)) {
         Path targetPath = Paths.get(targetFilePath);
         FileUtils.makeDirs(targetPath);
         Document document = null;
         PdfCopy copy = null;
         OutputStream os = null;

         try {
            os = new BufferedOutputStream(Files.newOutputStream(targetPath));
            document = new Document(new PdfReader(sourcePdfPaths[0]).getPageSize(1));
            copy = new PdfCopy(document, os);
            document.open();

            for (String sourceFilePath : sourcePdfPaths) {
               PdfReader reader = null;

               try {
                  reader = new PdfReader(sourceFilePath);
                  int n = reader.getNumberOfPages();

                  for (int j = 1; j <= n; j++) {
                     document.newPage();
                     PdfImportedPage page = copy.getImportedPage(reader, j);
                     copy.addPage(page);
                  }
               } catch (IOException var43) {
                  throw new FileException(var43);
               } finally {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Exception var42) {
                     }
                  }
               }
            }
         } catch (Exception var45) {
            throw new FileException(var45);
         } finally {
            if (copy != null) {
               try {
                  copy.close();
               } catch (Exception var41) {
               }
            }

            if (document != null) {
               try {
                  document.close();
               } catch (Exception var40) {
               }
            }

            if (os != null) {
               try {
                  os.close();
               } catch (Exception var39) {
               }
            }
         }
      }
   }

   public static void addStamp(File pdfFile, File picStampFile) {
      if (FileUtils.isImage(picStampFile) && FileUtils.isExtension(pdfFile, "pdf")) {
         PdfReader reader = null;
         String fileName = pdfFile.getAbsolutePath();

         try {
            reader = new PdfReader(pdfFile.getAbsolutePath());
         } catch (IOException var12) {
            throw new FileException(var12);
         } finally {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Exception var11) {
               }
            }
         }

         File temFile = getAddStampFile(reader, picStampFile.getAbsolutePath());
         temFile.renameTo(new File(fileName));
      } else {
         throw new UnexpectedException(I18nHelper.getMessage("GIKAM.EXCEPTION.FILE_FORMAT_WRONG"));
      }
   }

   public static void addText(FilePathDTO pdfFilePath, FilePathDTO destPdfFilePath, PdfUtils.TextPhraseConfig textPhraseConfig) {
      addText(
         FilePathManager.getLocalPath(pdfFilePath).normalize().toString(),
         FilePathManager.getLocalPath(destPdfFilePath).normalize().toString(),
         textPhraseConfig
      );
   }

   public static void addText(String pdfFilePath, String destPdfFilePath, PdfUtils.TextPhraseConfig textPhraseConfig) {
      com.itextpdf.text.Document document = null;
      com.itextpdf.text.pdf.PdfReader reader = null;
      PdfWriter writer = null;

      try {
         document = new com.itextpdf.text.Document();
         writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
         writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, com.itextpdf.text.Document doc) {
               Font font = FontFactory.getFont(textPhraseConfig.getFont(), "Identity-H", true, textPhraseConfig.getFontSize(), 0, textPhraseConfig.getColor());
               Phrase textPhrase = new Phrase(textPhraseConfig.getText(), font);
               ColumnText.showTextAligned(writer.getDirectContent(), 1, textPhrase, textPhraseConfig.getX(), textPhraseConfig.getY(), 0.0F);
            }
         });
         FileUtils.createFile(Paths.get(pdfFilePath));
         document.open();
         PdfContentByte pdfContent = writer.getDirectContent();
         reader = new com.itextpdf.text.pdf.PdfReader(pdfFilePath);
         int i = 1;

         for (int j = reader.getNumberOfPages(); i <= j; i++) {
            document.newPage();
            com.itextpdf.text.pdf.PdfImportedPage page = writer.getImportedPage(reader, i);
            pdfContent.addTemplate(page, 0.0F, 0.0F);
         }
      } catch (Exception var23) {
         throw new FileException(var23);
      } finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (Exception var22) {
            }
         }

         if (reader != null) {
            try {
               reader.close();
            } catch (Exception var21) {
            }
         }

         if (document != null) {
            try {
               document.close();
            } catch (Exception var20) {
            }
         }
      }
   }

   public static void addImage(FilePathDTO pdfFilePath, FilePathDTO destPdfFilePath, PdfUtils.ImageConfig imageConfig) {
      addImage(
         FilePathManager.getLocalPath(pdfFilePath).normalize().toString(), FilePathManager.getLocalPath(destPdfFilePath).normalize().toString(), imageConfig
      );
   }

   public static void addImage(String pdfFilePath, String destPdfFilePath, PdfUtils.ImageConfig imageConfig) {
      com.itextpdf.text.Document document = null;
      com.itextpdf.text.pdf.PdfReader reader = null;
      PdfWriter writer = null;

      try {
         document = new com.itextpdf.text.Document();
         writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
         writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, com.itextpdf.text.Document doc) {
               PdfPTable table = new PdfPTable(1);
               table.setTotalWidth(530.0F);
               PdfPCell cell = new PdfPCell();
               cell.setBorder(0);

               try {
                  Image image = Image.getInstance(imageConfig.getImgPath());
                  image.setWidthPercentage(20.0F);
                  cell.setPaddingTop(-20.0F);
                  cell.addElement(image);
               } catch (Exception var6) {
                  throw new FileException(var6);
               }

               table.addCell(cell);
               table.writeSelectedRows(0, -1, imageConfig.getX(), imageConfig.getY(), writer.getDirectContent());
            }
         });
         document.open();
         PdfContentByte pdfContent = writer.getDirectContent();
         FileUtils.createFile(Paths.get(destPdfFilePath));
         reader = new com.itextpdf.text.pdf.PdfReader(destPdfFilePath);
         int i = 1;

         for (int j = reader.getNumberOfPages(); i <= j; i++) {
            document.newPage();
            com.itextpdf.text.pdf.PdfImportedPage page = writer.getImportedPage(reader, i);
            pdfContent.addTemplate(page, 0.0F, 0.0F);
         }
      } catch (Exception var23) {
         throw new FileException(var23);
      } finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (Exception var22) {
            }
         }

         if (reader != null) {
            try {
               reader.close();
            } catch (Exception var21) {
            }
         }

         if (document != null) {
            try {
               document.close();
            } catch (Exception var20) {
            }
         }
      }
   }

   public static void addTextWatermark(String filePath, String destFilePath, String waterMark) {
      String fontPath = SystemUtils.getWorkingPath() + File.separator + "simsun.ttc,1";
      com.itextpdf.text.pdf.PdfReader reader = null;
      FileOutputStream fos = null;
      File destFile = null;
      PdfStamper stamper = null;

      try {
         reader = new com.itextpdf.text.pdf.PdfReader(filePath, "PDF".getBytes());
         destFile = new File(destFilePath);
         FileUtils.createFile(destFile);
         fos = new FileOutputStream(destFile);
         stamper = new PdfStamper(reader, fos);
         BaseFont font = BaseFont.createFont(fontPath, "Identity-H", true);
         int pageSize = reader.getNumberOfPages();

         for (int i = 1; i <= pageSize; i++) {
            PdfContentByte under = stamper.getUnderContent(i);
            under = stamper.getOverContent(i);
            under.beginText();
            under.setColorFill(BaseColor.LIGHT_GRAY);
            under.setFontAndSize(font, 50.0F);
            under.setTextMatrix(550.0F, 400.0F);
            under.showTextAligned(2, waterMark, 550.0F, 400.0F, 45.0F);
            under.endText();
         }
      } catch (DocumentException | IOException var25) {
         throw new UnexpectedException(var25);
      } finally {
         if (stamper != null) {
            try {
               stamper.close();
            } catch (Exception var24) {
            }
         }

         if (fos != null) {
            try {
               fos.close();
            } catch (Exception var23) {
            }
         }

         if (reader != null) {
            try {
               reader.close();
            } catch (Exception var22) {
            }
         }
      }
   }

   private static File getAddStampFile(PdfReader reader, String picPath) {
      File newFile = FileUtils.createTempFile("pdf");
      FileOutputStream os = null;
      com.lowagie.text.pdf.PdfStamper stamp = null;

      try {
         os = new FileOutputStream(newFile);
         stamp = new com.lowagie.text.pdf.PdfStamper(reader, os);
         Rectangle pageSize = reader.getPageSize(1);
         float height = pageSize.getHeight();
         float width = pageSize.getWidth();
         int nums = reader.getNumberOfPages();
         com.lowagie.text.Image[] nImage = splitImage(picPath, nums);

         for (int n = 1; n <= nums; n++) {
            com.lowagie.text.pdf.PdfContentByte over = stamp.getOverContent(n);
            com.lowagie.text.Image img = nImage[n - 1];
            img.setAbsolutePosition(width - img.getWidth(), height / 2.0F - img.getHeight() / 2.0F);
            over.addImage(img);
         }
      } catch (IOException | com.lowagie.text.DocumentException var23) {
         throw new FileException(var23);
      } finally {
         if (stamp != null) {
            try {
               stamp.close();
            } catch (Exception var22) {
            }
         }

         if (os != null) {
            try {
               os.close();
            } catch (Exception var21) {
            }
         }
      }

      return newFile;
   }

   private static com.lowagie.text.Image[] splitImage(String imgPath, int n) {
      com.lowagie.text.Image[] images = new com.lowagie.text.Image[n];
      BufferedImage img = null;

      try {
         img = ImageIO.read(new File(imgPath));
         int h = img.getHeight();
         int w = img.getWidth();
         int sw = w / n;

         for (int i = 0; i < n; i++) {
            BufferedImage subImg = null;
            if (i == n - 1) {
               subImg = img.getSubimage(i * sw, 0, w - i * sw, h);
            } else {
               subImg = img.getSubimage(i * sw, 0, sw, h);
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
               ImageIO.write(subImg, imgPath.substring(imgPath.lastIndexOf(46) + 1), out);
               images[i] = com.lowagie.text.Image.getInstance(out.toByteArray());
            }
         }

         return images;
      } catch (Exception var22) {
         throw new FileException(var22);
      }
   }

   public static class ImageConfig {
      private String imgPath;
      private float x;
      private float y;

      public String getImgPath() {
         return this.imgPath;
      }

      public void setImgPath(String imgPath) {
         this.imgPath = imgPath;
      }

      public float getX() {
         return this.x;
      }

      public void setX(float x) {
         this.x = x;
      }

      public float getY() {
         return this.y;
      }

      public void setY(float y) {
         this.y = y;
      }
   }

   public static class TextPhraseConfig {
      private String text;
      private float x;
      private float y;
      private String font = "simsun.ttc,0";
      private float fontSize;
      private BaseColor color;

      public String getText() {
         return this.text;
      }

      public void setText(String text) {
         this.text = text;
      }

      public float getX() {
         return this.x;
      }

      public void setX(float x) {
         this.x = x;
      }

      public float getY() {
         return this.y;
      }

      public void setY(float y) {
         this.y = y;
      }

      public String getFont() {
         return this.font;
      }

      public void setFont(String font) {
         this.font = font;
      }

      public BaseColor getColor() {
         return this.color;
      }

      public void setColor(BaseColor color) {
         this.color = color;
      }

      public float getFontSize() {
         return this.fontSize;
      }

      public void setFontSize(float fontSize) {
         this.fontSize = fontSize;
      }
   }
}
