package mannings.msi.com.smartipc.model;

public class Ipc2019 {
    private String symbol;
    private String kind;
    private String descricao;
    private String descricaobr;
    private String descricaofr;
    private int indice;
    private int proximo;

    public Ipc2019() {
    }

    public Ipc2019(String symbol, String kind, String descricao, String descricaobr, String descricaofr, int indice, int proximo) {
        this.symbol = symbol;
        this.kind = kind;
        this.descricao = descricao;
        this.descricaobr = descricaobr;
        this.descricaofr = descricaofr;
        this.indice = indice;
        this.proximo = proximo;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaobr() {
        return descricaobr;
    }

    public void setDescricaobr(String descricaobr) {
        this.descricaobr = descricaobr;
    }

    public String getDescricaofr() {
        return descricaofr;
    }

    public void setDescricaofr(String descricaofr) {
        this.descricaofr = descricaofr;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getProximo() {
        return proximo;
    }

    public void setProximo(int proximo) {
        this.proximo = proximo;
    }
}

