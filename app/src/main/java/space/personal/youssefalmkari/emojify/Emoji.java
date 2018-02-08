package space.personal.youssefalmkari.emojify;

public enum Emoji
{
    SMILE(R.drawable.smile),
    CLOSED_SMILE(R.drawable.closed_smile),
    FROWN(R.drawable.frown),
    CLOSED_FROWN(R.drawable.closed_frown),
    LEFT_WINK(R.drawable.leftwink),
    LEFT_WINK_FROWN(R.drawable.leftwinkfrown),
    RIGHT_WINK(R.drawable.rightwink),
    RIGHT_WINK_FROWN(R.drawable.rightwinkfrown);

    private int emoji;
    Emoji(int emoji) {
        this.emoji = emoji;
    }

    public int getEmoji() {
        return emoji;
    }

    public void setEmoji(int emoji) {
        this.emoji = emoji;
    }
}
