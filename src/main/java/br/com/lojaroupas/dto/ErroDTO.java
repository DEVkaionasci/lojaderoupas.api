package br.com.lojaroupas.dto;

public class ErroDTO {

    private int status;
    private String erro;
    private String timestamp;

    public ErroDTO() {
    }

    public ErroDTO(int status, String erro, String timestamp) {
        this.status = status;
        this.erro = erro;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
