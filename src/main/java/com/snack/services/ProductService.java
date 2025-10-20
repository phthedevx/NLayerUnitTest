package com.snack.services;

import com.snack.entities.Product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class ProductService {
    private String filePath = "C:\\Users\\aluno\\BancoImagens\\";

    private String getFileExtension(Path path) {
        String filename = path.getFileName().toString();
        int lastDotIndex = filename.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex + 1);
    }

    public boolean save(Product product) {
        Path path = Paths.get(product.getImage());

        Path destinationPath = Paths.get(String.format("%s%d.%s", filePath, product.getId(), getFileExtension(path)));

        if (Files.exists(path)) {
            try {
                Files.copy(path, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(destinationPath.toString());
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    public String getImagePathById(int id) {
        File directory = new File(filePath);
        File[] matches = directory.listFiles((dir, name) -> name.startsWith(String.valueOf(id)));
        return Arrays.stream(matches).findFirst().get().getAbsolutePath();
    }

    public void update(Product product) {
        remove(product.getId());
        save(product);
    }

    public void remove(int id) {
        Path path = Paths.get(getImagePathById(id));

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
