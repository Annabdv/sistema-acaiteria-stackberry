package com.stackberry.tamanho;

import com.stackberry.exception.ValidacaoException;
import io.javalin.http.Context;

import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TamanhoController {
    private final TamanhoService tamanhoService;

    public TamanhoController() {
        this.tamanhoService = new TamanhoService();
    }

    public void exibirFormularioCadastro(Context ctx){
        Tamanho tamanhoVazio = new Tamanho();

        Map<String, Object> modelo = Map.of("tamanho", tamanhoVazio);
        ctx.render("/admin/tamanhos/formulario-criar.html", modelo);
    }

    public void cadastrarTamanhoView(Context ctx){
        String nome = ctx.formParam("nome");
        String precoStr = ctx.formParam("preco");
        BigDecimal preco = (precoStr != null && !precoStr.trim().isEmpty()) ? new BigDecimal(precoStr.trim()) : BigDecimal.ZERO;
        String volume = ctx.formParam("volume");
        String descricao = ctx.formParam("descricao");
        String imagem = ctx.formParam("imagem");

        UploadedFile arquivo = ctx.uploadedFile("fotoArquivo");
        if (arquivo != null && arquivo.size() > 0) {
            try {
                String ext = arquivo.extension();
                if (ext == null || ext.isEmpty()) ext = ".jpg";
                if (!ext.startsWith(".")) ext = "." + ext;

                String nomeArquivo = "prod_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

                File dir = new File("uploads");
                if (!dir.exists()) dir.mkdirs();

                File destino = new File(dir, nomeArquivo);
                try (InputStream in = arquivo.content();
                     FileOutputStream out = new FileOutputStream(destino)) {
                    in.transferTo(out);
                }

                imagem = "/uploads/" + nomeArquivo;
            } catch (Exception e) {
                System.err.println("Erro ao salvar foto de upload: " + e.getMessage());
            }
        }

        Tamanho tamanhoDoFormulario = new Tamanho(nome, preco, volume, descricao, imagem);

        try {
            tamanhoService.cadastrarTamanho(tamanhoDoFormulario);
            ctx.redirect("/admin/dashboard_admin");
        } catch (ValidacaoException e) {
            ctx.status(400).result("Erro de validação: " + e.getMessage());
        }
    }


    public void exibirPaginaDeTamanhos(Context ctx){

        List<Tamanho> tamanhoList = tamanhoService.listarTamanhos(); // Crio minha lista de tamanhos

        // Faço um modelo para ser usando pelo th:each lá no "tamanho.html"
        Map<String, Object> modelo = Map.of("tamanhos", tamanhoList);
        ctx.render("admin/tamanhos/tamanho.html", modelo); // Mostro a página e passo o modelo.
    }

    public void listarTamanhoApi(Context ctx){
        List<Tamanho> tamanhoList = tamanhoService.listarTamanhos();
        ctx.json(tamanhoList);
    }

   public void exibirFormularioDeEdicao(Context ctx){

        String id = ctx.pathParam("id"); // Recebe do tamanho.html
       Optional<Tamanho> tamanhoOptional = tamanhoService.buscarPorId(id);

       if(tamanhoOptional.isPresent()){
           // Basicamente, passo o model para já ficar preenchido bonitinho para meu formulario-editar.html
           Map<String, Object> modelo = Map.of("tamanho", tamanhoOptional.get());
           ctx.render("/admin/tamanhos/formulario-editar.html", modelo);
       } else {
           ctx.status(404).result("Tamanho não encontrado");
       }
   }

   public void atualizarTamanho(Context ctx){
        String id = ctx.formParam("id");
        String nome = ctx.formParam("nome");
        String precoStr = ctx.formParam("preco");
        BigDecimal preco = (precoStr != null && !precoStr.trim().isEmpty()) ? new BigDecimal(precoStr.trim()) : BigDecimal.ZERO;
        String volume = ctx.formParam("volume");
        String descricao = ctx.formParam("descricao");
        String imagem = ctx.formParam("imagem");

        UploadedFile arquivo = ctx.uploadedFile("fotoArquivo");
        if (arquivo != null && arquivo.size() > 0) {
            try {
                String ext = arquivo.extension();
                if (ext == null || ext.isEmpty()) ext = ".jpg";
                if (!ext.startsWith(".")) ext = "." + ext;

                String nomeArquivo = "prod_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

                File dir = new File("uploads");
                if (!dir.exists()) dir.mkdirs();

                File destino = new File(dir, nomeArquivo);
                try (InputStream in = arquivo.content();
                     FileOutputStream out = new FileOutputStream(destino)) {
                    in.transferTo(out);
                }

                imagem = "/uploads/" + nomeArquivo;
            } catch (Exception e) {
                System.err.println("Erro ao salvar foto atualizada de upload: " + e.getMessage());
            }
        }

        Tamanho tamanhoAtualizado = new Tamanho(nome, preco, volume, descricao, imagem);
        tamanhoAtualizado.setId(id);
        tamanhoService.atualizarTamanho(id, tamanhoAtualizado);
        ctx.redirect("/admin/dashboard_admin");
   }

   public void removerTamanho(Context ctx){
        String id = ctx.pathParam("id");
        tamanhoService.removerTamanho(id);
        ctx.redirect("/admin/tamanhos");
   }

   public void removerTamanhoApi(Context ctx){
        String id = ctx.pathParam("id");

      try {
          tamanhoService.removerTamanho(id);
          ctx.status(200).json(Map.of("mensagem", "Tamanho removido com sucesso"));
      } catch (Exception e) {
          ctx.status(500).json(Map.of("mensage", "Erro ao remover taamnho") + e.getMessage());
      }
   }




}
