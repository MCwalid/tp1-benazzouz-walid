package ma.emsi.benazzouzwalid.tp2benazzouzwalid.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.benazzouzwalid.tp2benazzouzwalid.llm.JsonUtilPourGemini;
import ma.emsi.benazzouzwalid.tp2benazzouzwalid.llm.LlmInteraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class Bb implements Serializable {

    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    private boolean debug = false;
    private String texteRequeteJson;
    private String texteReponseJson;

    @Inject
    private FacesContext facesContext;

    @Inject
    private JsonUtilPourGemini jsonUtil;

    public Bb() {}

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void toggleDebug() {
        this.setDebug(!isDebug());
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    public String getRoleSysteme() {
        return roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return roleSystemeChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        try {
            if (conversation.isEmpty()) {
                jsonUtil.setSystemRole(roleSysteme);
                this.roleSystemeChangeable = false;
            }

            LlmInteraction interaction = jsonUtil.envoyerRequete(question);
            this.reponse = interaction.reponseExtraite();
            this.texteRequeteJson = interaction.questionJson();
            this.texteReponseJson = interaction.reponseJson();

            afficherConversation();

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM",
                    "Problème : " + e.getMessage());
            facesContext.addMessage(null, message);
        }

        return null;
    }

    public String nouveauChat() {
        return "index";
    }

    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question)
                .append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();

            String role = """
                You are a helpful assistant. You help the user to find the information they need.
                If the user types a question, you answer it.
                """;
            this.listeRolesSysteme.add(new SelectItem(role, "Assistant"));

            role = """
                You are an interpreter. You translate from English to French and from French to English.
                If the user types a French text, you translate it into English.
                If the user types an English text, you translate it into French.
                """;
            this.listeRolesSysteme.add(new SelectItem(role, "Traducteur Anglais-Français"));

            role = """
                You are a travel guide. If the user types the name of a country or a city,
                you tell them what are the main places to visit and their costs.
                """;
            this.listeRolesSysteme.add(new SelectItem(role, "Guide touristique"));

            // ✅ NOUVEAU RÔLE SYSTÈME : Citoyen du Futur
            role = """
                You are a normal human living in the year 2150. You speak as if you are answering from the future.
                You describe future technologies, futuristic football, daily life, transport, and culture.
                Your tone is optimistic, realistic and immersive. You never say you are an AI, only a citizen of 2150.
                """;
            this.listeRolesSysteme.add(new SelectItem(role, "Citoyen du futur (2150)"));
        }
        return this.listeRolesSysteme;
    }

}
