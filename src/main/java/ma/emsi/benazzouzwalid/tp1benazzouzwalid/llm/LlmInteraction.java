package ma.emsi.benazzouzwalid.tp1benazzouzwalid.llm;

/**
 * Conteneur immuable pour une interaction LLM.
 * - questionJson: JSON envoyé (joli/pretty)
 * - reponseJson: JSON reçu (brut)
 * - reponseExtraite: texte à afficher à l'utilisateur
 */
public record LlmInteraction(String questionJson, String reponseJson, String reponseExtraite) { }
