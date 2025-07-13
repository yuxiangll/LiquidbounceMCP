package net.ccbluex.liquidbounce.ui.client.lunar.font;

public enum FontUtil {
	
	TITLE (new CustomFontRenderer("title", 15)),
	TEXT (new CustomFontRenderer("", 11)),
	TEXT_BOLD (new CustomFontRenderer("text_bold", 9));
	
	private CustomFontRenderer font;
	
	private FontUtil(CustomFontRenderer font) {
		this.font = font;
	}
	
	public CustomFontRenderer getFont() {
		return font;
	}

}
