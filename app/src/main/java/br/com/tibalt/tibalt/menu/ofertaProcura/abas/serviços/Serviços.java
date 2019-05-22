package br.com.tibalt.tibalt.menu.ofertaProcura.abas.serviços;

import android.graphics.Bitmap;

import java.util.Date;

public class Serviços {

    float preco1, preco2;
    String titulo, descricao, nomeResponsavel;
    private long idProduto, idResponsavel;
    int tipoPreco;
    private Date dataPublicacao;
    Bitmap[] imagens;

    public float getPreco1() {
        return preco1;
    }

    public void setPreco1(float preco1) {
        this.preco1 = preco1;
    }

    public float getPreco2() {
        return preco2;
    }

    public void setPreco2(float preco2) {
        this.preco2 = preco2;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(long idProduto) {
        this.idProduto = idProduto;
    }

    public long getIdResponsavel() {
        return idResponsavel;
    }

    public void setIdResponsavel(long idResponsavel) {
        this.idResponsavel = idResponsavel;
    }

    public int getTipoPreco() {
        return tipoPreco;
    }

    public void setTipoPreco(int tipoPreco) {
        this.tipoPreco = tipoPreco;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Bitmap[] getImagens() {
        return imagens;
    }

    public void setImagens(Bitmap[] imagens) {
        this.imagens = imagens;
    }

}