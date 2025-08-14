package br.ufpb.br.grupo02.projeto.form.model.validadores;

import br.ufpb.br.grupo02.projeto.form.model.ResultadoValidacao;
import br.ufpb.br.grupo02.projeto.form.model.ValidadorCampo;

public class ValidadorEmail implements ValidadorCampo {

    @Override
    public ResultadoValidacao validarCampo(String valor) {
        if (valor == null || valor.isEmpty()) {
            return new ResultadoValidacao("O email não pode ser vazio");
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!valor.matches(emailRegex)) {
            return new ResultadoValidacao("Informe um email válido");
        }
        return new ResultadoValidacao();
    }
}
