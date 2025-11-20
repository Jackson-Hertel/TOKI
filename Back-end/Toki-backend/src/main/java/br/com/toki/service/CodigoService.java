package br.com.toki.service;

import java.util.Random;

public class CodigoService {
    public static String gerarCodigo(int tamanho) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < tamanho; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
