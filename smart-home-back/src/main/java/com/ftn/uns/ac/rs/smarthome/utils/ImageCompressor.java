package com.ftn.uns.ac.rs.smarthome.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import org.apache.commons.io.FilenameUtils;


public class ImageCompressor {
    public static File compressImage(File image, float qualityOfOutputImage) {
        String tempFilePath = "src/main/resources/temp/";
        String imageFile = image.getName();
        try {
            String extension = FilenameUtils.getExtension(imageFile);
            BufferedImage originalImage = ImageIO.read(image);

            ImageWriteParam imageWriteParam = ImageIO.getImageWritersByFormatName(extension).next().getDefaultWriteParam();

            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            imageWriteParam.setCompressionQuality(qualityOfOutputImage);

            Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(extension);
            ImageWriter imageWriter = imageWriters.next();
            File source = new File(tempFilePath + "cmp_" + imageFile);

            imageWriter.setOutput(ImageIO.createImageOutputStream(source));
            imageWriter.write(null, new IIOImage(originalImage, null, null), imageWriteParam);
            imageWriter.dispose();
            image.delete();
            return new File(tempFilePath + image.getName());

        } catch (IOException e) {
            try{
                File file = new File(tempFilePath + image.getName());
                Files.deleteIfExists(file.toPath());
                System.out.println(e.getMessage());
                return  null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}
