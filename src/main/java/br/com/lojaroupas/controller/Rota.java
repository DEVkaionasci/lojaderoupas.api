package br.com.lojaroupas.controller;

import br.com.lojaroupas.command.Command;

import java.util.regex.Pattern;

/** Uma linha da "tabela de rotas": metodo HTTP + padrao de URL + Command + se exige chave admin. */
public class Rota {

    private final String metodo;
    private final Pattern padrao;
    private final boolean protegida;
    private final Command command;

    public Rota(String metodo, String regex, boolean protegida, Command command) {
        this.metodo = metodo;
        this.padrao = Pattern.compile(regex);
        this.protegida = protegida;
        this.command = command;
    }

    public String getMetodo() {
        return metodo;
    }

    public Pattern getPadrao() {
        return padrao;
    }

    public boolean isProtegida() {
        return protegida;
    }

    public Command getCommand() {
        return command;
    }
}
