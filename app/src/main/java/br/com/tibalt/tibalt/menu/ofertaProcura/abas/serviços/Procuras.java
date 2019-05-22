package br.com.tibalt.tibalt.menu.ofertaProcura.abas.serviços;

import android.graphics.Bitmap;

public class Procuras extends Serviços{


    public Procuras(String titulo, String descricao, int tipoPreco, float preco1, float preco2, Bitmap[] imagens) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipoPreco = tipoPreco;
        this.preco1 = preco1;
        this.preco2 = preco2;
        this.imagens = imagens;
    }

    public Procuras() { }
}
