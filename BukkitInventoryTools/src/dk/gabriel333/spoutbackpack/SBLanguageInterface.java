package dk.gabriel333.spoutbackpack;

public class SBLanguageInterface {
	public Language					language	= Language.ENGLISH;

	private SBLanguageInterface_EN	languageEN	= new SBLanguageInterface_EN();
	private SBLanguageInterface_FR	languageFR	= new SBLanguageInterface_FR();

	public Language getLanguage() {
		return language;
	}

	public SBLanguageInterface(Language language2) {
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
