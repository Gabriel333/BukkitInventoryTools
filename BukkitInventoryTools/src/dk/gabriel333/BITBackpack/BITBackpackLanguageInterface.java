package dk.gabriel333.BITBackpack;


public class BITBackpackLanguageInterface {
	public Language					language	= Language.ENGLISH;

	private BITLanguageInterface_EN	languageEN	= new BITLanguageInterface_EN();
	private BITLanguageInterface_FR	languageFR	= new BITLanguageInterface_FR();

	public Language getLanguage() {
		return language;
	}

	public BITBackpackLanguageInterface(Language language2) {
		language = language2;
	}

	public String getMessage(String key) {
		switch (language) {
		case ENGLISH:
			return languageEN.getString(key);
		case FRENCH:
			return languageFR.getString(key);
		default:
			return languageEN.getString(key);
		}
	}

	public enum Language {
		ENGLISH, FRENCH;

		@Override
		public String toString() {
			return super.toString();
		}
	}
}
