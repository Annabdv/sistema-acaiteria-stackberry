package com.stackberry.admin;

import io.javalin.http.Context;

public class DashboardController {

    // Página principal da dashboard admin
    public void exibirDashboard(Context ctx) {
        ctx.render("admin/dashboard_admin.html");
    }

}