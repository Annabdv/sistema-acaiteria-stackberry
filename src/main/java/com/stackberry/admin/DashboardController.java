package com.stackberry.admin;

import com.stackberry.login.Usuario;
import io.javalin.http.Context;

public class DashboardController {

    // Página principal da dashboard admin
    public void exibirDashboard(Context ctx) {
        Usuario usuarioLogado = ctx.sessionAttribute("usuarioLogado");

        ctx.attribute("usuario", usuarioLogado);

        ctx.render("admin/dashboard_admin.html");
    }

}