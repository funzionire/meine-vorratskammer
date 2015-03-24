/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static controller.BeanFactory.getManageBeanStocks;
import static controller.BeanFactory.getManageBeanUserHousehold;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.AppUser;
import model.Household;
import model.Place;
import model.StocksArticle;
import model.StocksUnit;

/**
 *
 * @author baader
 */
/*In der Homepage.jsp gibt es einen Button, der zum Erzeugen eines neuen Haushaltes genutzt werden soll
 Dafür musste ein neuer Step eingefügt werden (kann man auch gern noch umbenennen)
 bei dem muss dann die Methode createHoushold aufgerufen werden
 */
@WebServlet(name = "ControllerServlet", urlPatterns = {"/ControllerServlet"})
public class ControllerServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(ControllerServlet.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try{
            LOG.info("CustomInfo: SessionBean initialisieren");
            ManageBeanUserHouseholdLocal manageBeanUserHousehold = getManageBeanUserHousehold();
            ManageBeanStocksLocal manageBeanStocks = getManageBeanStocks();
            String currentStep = request.getParameter("step");
            LOG.info("CustomInfo: Aktueller Schritt:" + currentStep);

            /*-------------------------------------------------------------------------------------------
            User (login, registrieren, löschen, ändern, logout)
            -------------------------------------------------------------------------------------------*/
            if(currentStep == null || currentStep.equals("login")){
                if(request.getParameter("email").equals("")){
                    throw new MVKException("Bitte geben Sie eine Email an.");
                }
                if(request.getParameter("password").equals("")){
                    throw new MVKException("Bitte geben Sie eine Passwort an.");
                }
                AppUser user = manageBeanUserHousehold.login(request.getParameter("email"),
                        request.getParameter("password"));
                if (user != null) {
                    LOG.info("CustomInfo: Email und Passwort korrekt");
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", user);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
                } else {
                    LOG.info("CustomInfo: Email oder Passwort falsch");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("register")){
                if(request.getParameter("name") == null){
                    throw new MVKException("Bitte geben Sie einen Namen an.");
                }
                if(request.getParameter("email") == null){
                    throw new MVKException("Bitte geben Sie eine Email an.");
                }
                if(request.getParameter("password") == null){
                    throw new MVKException("Bitte geben Sie eine Passwort an.");
                }
                AppUser user = manageBeanUserHousehold.createUser(request.getParameter("name"),
                        request.getParameter("email"),
                        request.getParameter("password"));
                if (user != null) {
                    LOG.info("CustomInfo: Registrierung erfolgreich");
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", user);
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
                } else {
                    LOG.info("CustomInfo: Registrierung fehlgeschlagen");
                    request.getRequestDispatcher("/register.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("deleteUser")){
                HttpSession session = request.getSession(true);
                
                //hier muss es noch angepasst werden --> zwecks void oder boolean!!
                Boolean isDeleted = true;
                
                manageBeanUserHousehold.deleteUser((AppUser)session.getAttribute("user"));
                if(isDeleted == true){
                    LOG.info("CustomInfo: User erfolgreich gelöscht");
                    session.setAttribute("user", null);
                    request.getRequestDispatcher("/index.jsp");
                }
                else{
                    LOG.info("CustomInfo: User löschen fehlgeschlagen");
                    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
                } 
            }
            else if(currentStep.equals("changeUser")){
                HttpSession session = request.getSession(true);
                AppUser currentUser = (AppUser)session.getAttribute("user");
                String name = (String)request.getParameter("name");
                LOG.info(name);
                String email = (String)request.getParameter("email");
                String password = (String)request.getParameter("password");
                AppUser changedUser = null;
                if(!name.equals("")){
                    changedUser = manageBeanUserHousehold.changeName(currentUser, name);
                }
                else if(!email.equals("")){
                    changedUser = manageBeanUserHousehold.changeEmail(currentUser, email);
                }
                else if(!password.equals("")){
                    changedUser = manageBeanUserHousehold.changePassword(currentUser, password);
                }

                if(changedUser != null){
                    LOG.info("CustomInfo: User erfolgreich geändert");
                    request.setAttribute("user", changedUser);
                    session.setAttribute("user", changedUser);
                    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
                }
                else{
                    LOG.info("CustomInfo: User ändern fehlgeschlagen");
                    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("logout")){
                HttpSession session = request.getSession(true);
                session.invalidate();
                LOG.info("CustomInfo: Ausloggen erfolgreich");
                request.getRequestDispatcher("/logout.jsp").forward(request, response);
            }

            /*-------------------------------------------------------------------------------------------
            Household
            -------------------------------------------------------------------------------------------*/
            else if(currentStep.equals("createHousehold")){
                HttpSession session = request.getSession(true);
                Household household = manageBeanUserHousehold.addHousehold(request.getParameter("name"),
                                                                        (AppUser) session.getAttribute("user"));
                if (household != null) {
                    LOG.info("CustomInfo: Haushalt erfolgreich angelegt");
                    session.setAttribute("household", household);
                    request.setAttribute("user", (AppUser) session.getAttribute("user"));
                    request.setAttribute("household", household);
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                } else {
                    LOG.info("CustomInfo: Haushalt anlegen fehlgeschlagen");
                    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("changeHousehold")){
                HttpSession session = request.getSession(true);
                Household changedHousehold = manageBeanUserHousehold.changeHousehold((Household)session.getAttribute("household"),
                                                                                     (String)request.getAttribute("name"));
                if(changedHousehold != null){
                    LOG.info("CustomInfo: Haushalt erfolgreich geändert");
                    request.setAttribute("household", changedHousehold);
                    session.setAttribute("household", changedHousehold);
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
                else{
                    LOG.info("CustomInfo: Haushalt ändern fehlgeschlagen");
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                }       
            }
            //muss noch auf removeHousehold geändert werden
            else if(currentStep.equals("removeHousehold")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: Haushalt entfernen (ControllerServlet)");
                AppUser user = manageBeanUserHousehold.removeHousehold((Household)session.getAttribute("household"),(AppUser)session.getAttribute("user"));
                request.setAttribute("user", user);
//                request.setAttribute("user", session.getAttribute("user"));
//                request.setAttribute("household", session.getAttribute("household"));
//                response.sendRedirect("/MVK-war/ControllerServlet?step=toHomepage");
                request.getRequestDispatcher("homepage.jsp").forward(request, response);
            }
            else if(currentStep.equals("shareHousehold")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: Haushalt teilen");
                String email = (String)request.getParameter("email");
                manageBeanUserHousehold.shareHousehold((Household) session.getAttribute("household"), email);

                request.setAttribute("user", session.getAttribute("user"));
                request.setAttribute("household", session.getAttribute("household"));
                request.getRequestDispatcher("/household.jsp").forward(request, response);

            }
            /*-------------------------------------------------------------------------------------------
            Place
            -------------------------------------------------------------------------------------------*/
            else if(currentStep.equals("createPlace")){
                HttpSession session = request.getSession(true);
                Place place = manageBeanUserHousehold.addPlace(request.getParameter("name"),
                                                            (Household) session.getAttribute("household"));
                if (place != null) {
                    LOG.info("CustomInfo: Lagerort erfolgreich angelegt");
                    session.setAttribute("place", place);
                    request.setAttribute("user", (AppUser) session.getAttribute("user"));
                    request.setAttribute("household", (Household) session.getAttribute("household"));
                    request.setAttribute("place", place);
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                } else {
                    LOG.info("CustomInfo: Lagerort anlegen fehlgeschlagen");
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
            }
            /*-------------------------------------------------------------------------------------------
            StocksArticle
            -------------------------------------------------------------------------------------------*/
            else if(currentStep.equals("createStocksArticle")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: ID vom Platz holen");
                Place place = manageBeanUserHousehold.findPlace((String)request.getParameter("id"));
                session.setAttribute("place", place);
                StocksArticle stocksArticle = manageBeanStocks.addStocksArticle(request.getParameter("name"),
                                                place, "");
                if (stocksArticle != null) {
                    LOG.info("CustomInfo: StocksArticle erfolgreich angelegt");
                    session.setAttribute("stocksArticle", stocksArticle);
                    request.setAttribute("user", (AppUser) session.getAttribute("user"));
                    request.setAttribute("household", (Household) session.getAttribute("household"));
                    request.setAttribute("place", (Place) session.getAttribute("place"));
                    request.setAttribute("stocksArticle", stocksArticle);
    //                request.setAttribute("id", session.getAttribute("id"));
                    response.sendRedirect("/MVK-war/ControllerServlet?step=toHousehold&id=" + session.getAttribute("id"));
    //                request.getRequestDispatcher("/household.jsp").forward(request, response);
                } else {
                    LOG.info("CustomInfo: StocksArticle anlegen fehlgeschlagen");
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("changeStocksArticle")){
                HttpSession session = request.getSession(true);
                session.getAttribute("stocksArticle");
            }
            /*-------------------------------------------------------------------------------------------
            StocksUnit
            -------------------------------------------------------------------------------------------*/
            else if(currentStep.equals("createStocksUnit")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: ID vom StocksArticle holen");              
                StocksArticle stocksArticle = manageBeanStocks.findStocksArticle((String)request.getParameter("StocksArticleID"));
                session.setAttribute("stocksArticle", stocksArticle);
                
                StocksUnit stocksUnit = manageBeanStocks.addStocksUnit(stocksArticle, request.getParameter("Menge"),
                                                request.getParameter("Datum"), request.getParameter("Kommentar"));
                if (stocksUnit != null) {
                    LOG.info("CustomInfo: StocksUnit erfolgreich angelegt");
                    session.setAttribute("stocksUnit", stocksUnit);
                    request.setAttribute("user", (AppUser) session.getAttribute("user"));
                    request.setAttribute("household", (Household) session.getAttribute("household"));
                    request.setAttribute("place", (Place) session.getAttribute("place"));
                    request.setAttribute("stocksArticle", (StocksArticle) session.getAttribute("stocksArticle"));
                    request.setAttribute("stocksUnit", stocksUnit);
    //                request.setAttribute("id", session.getAttribute("id"));
                    response.sendRedirect("/MVK-war/ControllerServlet?step=toHousehold&id=" + session.getAttribute("id"));
    //                request.getRequestDispatcher("/household.jsp").forward(request, response);
                } else {
                    LOG.info("CustomInfo: StocksArticle anlegen fehlgeschlagen");
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("raiseQuantity")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: Menge des Units erhöhen"); 
                
                //TODO: funktioniert noch nicht. Änderungen an der Methode raiseQuantity durchgeführt, da sich die Menge sonst nicht geändert hatte
                //      jetzt kann kein unit Objekt zurückgegeben werden -- warum?!
                StocksUnit changedUnit = manageBeanStocks.raiseQuantityOfStocksUnit((StocksUnit) session.getAttribute("stocksUnit"));
                if (changedUnit != null){
                    LOG.info("CustomInfo: Menge des Units wurde erhöht"); 
                    request.setAttribute("user", (AppUser) session.getAttribute("user"));
                    request.setAttribute("household", (Household) session.getAttribute("household"));
                    request.setAttribute("place", (Place) session.getAttribute("place"));
                    request.setAttribute("stocksArticle", (StocksArticle) session.getAttribute("stocksArticle"));
                    request.setAttribute("stocksUnit", changedUnit);
                    session.setAttribute("stocksUnit", changedUnit);
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
                else{
                     LOG.info("CustomInfo: StocksUnit erhöhen fehlgeschlagen");
                     request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("reduceQuantity")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: Menge des Units reduzieren"); 
                
                StocksUnit changedUnit = manageBeanStocks.reduceQuantityOfStocksUnit((StocksUnit) session.getAttribute("stocksUnit"));
                if (changedUnit != null){
                    LOG.info("CustomInfo: Menge des Units wurde erhöht"); 
                    request.setAttribute("user", (AppUser) session.getAttribute("user"));
                    request.setAttribute("household", (Household) session.getAttribute("household"));
                    request.setAttribute("place", (Place) session.getAttribute("place"));
                    request.setAttribute("stocksArticle", (StocksArticle) session.getAttribute("stocksArticle"));
                    request.setAttribute("stocksUnit", changedUnit);
                    session.setAttribute("stocksUnit", changedUnit);
                    request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
                else{
                     LOG.info("CustomInfo: StocksUnit erhöhen fehlgeschlagen");
                     request.getRequestDispatcher("/household.jsp").forward(request, response);
                }
            }
            else if(currentStep.equals("changeStocksArticle")){
                HttpSession session = request.getSession(true);
                session.getAttribute("stocksArticle");
            }

            /*-------------------------------------------------------------------------------------------
                Navigation
            -------------------------------------------------------------------------------------------*/
            else if(currentStep.equals("toHousehold")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: Haushalt öffnen");
                Household household = manageBeanUserHousehold.findHousehold((String)request.getParameter("id"));

                request.setAttribute("user", session.getAttribute("user"));
                request.setAttribute("household", household);
                session.setAttribute("id", household.getHouseholdID()); //hier war vorher longID ?? passt so auch oder?
                session.setAttribute("household", household);
                request.getRequestDispatcher("/household.jsp").forward(request, response);
            }
    //        else if(currentStep.equals("toPlace")){
    //            HttpSession session = request.getSession(true);
    //            LOG.info("CustomInfo: Platz öffnen");
    //            String id = (String)request.getParameter("id");
    //            long longID = Long.parseLong(id);
    //            Place place = manageBeanUserHousehold.findPlace(longID);
    //            
    //            request.setAttribute("user", session.getAttribute("user"));
    //            request.setAttribute("household", session.getAttribute("household"));
    //            request.setAttribute("place", place);
    //            session.setAttribute("place", place);
    //            request.getRequestDispatcher("/place.jsp").forward(request, response);
    //        }
    //        else if(currentStep.equals("toStocksArticle")){
    //            HttpSession session = request.getSession(true);
    //            LOG.info("CustomInfo: StocksArticle öffnen");
    //            request.setAttribute("user", session.getAttribute("user"));
    //            request.setAttribute("household", session.getAttribute("household"));
    //            request.setAttribute("place", session.getAttribute("place"));
    //            request.setAttribute("stocksArticle", session.getAttribute("stocksArticle"));
    //            request.getRequestDispatcher("/household.jsp").forward(request, response);
    //        }
            else if(currentStep.equals("toSettings")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: Einstellungen öffnen");
                request.setAttribute("user", session.getAttribute("user"));
                request.getRequestDispatcher("/settings.jsp").forward(request, response);
            }
            else if(currentStep.equals("toAbout")){
                LOG.info("CustomInfo: UeberUns öffnen");
                request.getRequestDispatcher("/about.jsp").forward(request, response);
            }
            else if(currentStep.equals("toHomepage")){
                HttpSession session = request.getSession(true);
                LOG.info("CustomInfo: Einstellungen öffnen");
                request.setAttribute("user", session.getAttribute("user"));
                request.getRequestDispatcher("/homepage.jsp").forward(request, response);
            }
        } catch (MVKException e){
            LOG.info("Exception geworfen");
//            request.setAttribute("errorText", e.getMessage());
            response.sendError(100, e.getMessage());
            //response.sendRedirect("/MVK-war/error.jsp?");
//            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
