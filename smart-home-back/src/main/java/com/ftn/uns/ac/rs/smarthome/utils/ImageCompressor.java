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
import java.util.Locale;
import java.util.NoSuchElementException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class ImageCompressor {
    public static File compressImage(File image, float qualityOfOutputImage, String username) {
        String tempFilePath = "../temp/";
        String imageFile = image.getName();
        String extension = FilenameUtils.getExtension(imageFile);
        String finalNamePath = tempFilePath + username + "." + extension;
        try {
            BufferedImage originalImage = ImageIO.read(image);

            ImageWriteParam imageWriteParam = ImageIO.getImageWritersByFormatName(extension).next().getDefaultWriteParam();

            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            imageWriteParam.setCompressionQuality(qualityOfOutputImage);

            Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(extension);
            ImageWriter imageWriter = imageWriters.next();
            File source = new File(finalNamePath);

            imageWriter.setOutput(ImageIO.createImageOutputStream(source));
            imageWriter.write(null, new IIOImage(originalImage, null, null), imageWriteParam);
            imageWriter.dispose();
            image.delete();
            return new File(finalNamePath);

        } catch (IOException e) {
            try{
                image.delete();
                File file = new File(finalNamePath);
                Files.deleteIfExists(file.toPath());
                System.out.println(e.getMessage());
                return  null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        catch(NoSuchElementException ex) {
            image.delete();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image format not supported!");
        }
    }
}
