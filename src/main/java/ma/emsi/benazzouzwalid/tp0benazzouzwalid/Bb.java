package ma.emsi.benazzouzwalid.tp0benazzouzwalid;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Backing bean pour la page JSF index.xhtml.
 * Portée view pour conserver l'état de la conversation qui dure pendant plusieurs requêtes HTTP.
 * La portée view nécessite l'implémentation de Serializable (le backing bean peut être mis en mémoire secondaire).
 */
@Named
@ViewScoped
public class Bb implements Serializable {

    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    @Inject
    private FacesContext facesContext;

    public Bb() {
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

    /**
     * Envoie la question au serveur.
     * Nouveau traitement personnel : analyse simple du ton du message.
     */
    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        // ✅ Nouveau traitement personnalisé :
        // Analyse de ton simple : si la phrase contient des mots positifs ou négatifs.
        String texte = question.toLowerCase(Locale.FRENCH);
        int score = 0;

        String[] positifs = {"bien", "génial", "super", "merci", "heureux", "parfait", "content", "cool"};
        String[] negatifs = {"mauvais", "triste", "problème", "bug", "erreur", "fatigué", "difficile"};

        for (String mot : positifs) {
            if (texte.contains(mot)) score += 2;
        }
        for (String mot : negatifs) {
            if (texte.contains(mot)) score -= 2;
        }

        String humeur;
        if (score > 2) {
            humeur = "😊 Ton message semble positif !";
        } else if (score < 0) {
            humeur = "😟 Ton message semble un peu négatif...";
        } else {
            humeur = "😐 Ton message est neutre.";
        }

        // Construction de la réponse finale
        this.reponse = "🔎 Analyse du ton : " + humeur + "\n📊 Score d’humeur : " + score;

        // Si la conversation commence, inclure le rôle système
        if (this.conversation.isEmpty()) {
            this.reponse = roleSysteme.toUpperCase(Locale.FRENCH) + "\n" + this.reponse;
            this.roleSystemeChangeable = false;
        }

        // Ajout à la conversation
        afficherConversation();
        return null;
    }

    /**
     * Pour un nouveau chat.
     */
    public String nouveauChat() {
        return "index";
    }

    /**
     * Pour afficher la conversation dans le textArea de la page JSF.
     */
    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();
            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Assistant"));

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Traducteur Anglais-Français"));

            role = """
                    You are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit and the average price of a meal.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Guide touristique"));
        }

        return this.listeRolesSysteme;
    }

}
