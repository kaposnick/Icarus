package input;

public class Configuration {

    private final String command;

    private long nodeId;
    private boolean cheater;
    private boolean enable = true;
    private boolean disable;
    private int x, y;

    public Configuration(final String cmnd) {
        this.command = cmnd;
    }

    public void parse() {
        String tokens[] = command.split(" ");
        this.nodeId = Long.parseLong(tokens[0]);

        for (int index = 1; index < tokens.length; index++) {
            String token = tokens[index];
            if (token.length() < 2) continue;

            switch (token.charAt(1)) {
                case 'c':
                    setCheater(token.charAt(2));
                    break;
                case 'e':
                    setEnabled(true);
                    break;
                case 'd':
                    setEnabled(false);
                    break;
                case 'x':
                    setX(token);
                    break;
                case 'y':
                    setY(token);
                    break;
                default:
                    break;
            }
        }
    }

    private void setY(String token) {
        this.y = Integer.parseInt(token.substring(3));
    }

    private void setX(String token) {
        this.x = Integer.parseInt(token.substring(3));
    }

    private void setEnabled(boolean enabled) {
        enable = enabled;
        disable = !enabled;
    }

    private void setCheater(char isCheater) {
        if (isCheater == '0') {
            cheater = false;
        } else cheater = true;
    }
}
