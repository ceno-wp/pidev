package tn.esprit.utils;

import tn.esprit.controllers.ManageUserController;

public class SessionManager {
    private static ManageUserController.User currentUser;

    public static void loginUser(ManageUserController.User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static ManageUserController.User getCurrentUser() {
        return currentUser;
    }

    public static boolean isClient() {
        return currentUser != null && currentUser.getRole().contains("ROLE_CLIENT");
    }

    public static boolean isLawyer() {
        return currentUser != null && currentUser.getRole().contains("ROLE_LAWYER");
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole().contains("ROLE_ADMIN");
    }
}