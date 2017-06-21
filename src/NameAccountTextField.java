import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class NameAccountTextField extends JTextField {

    public NameAccountTextField(int columns){
        super(columns);
    }

    @Override
    protected Document createDefaultModel() {
        return new NameAccountPlainDocument();
    }

    private class NameAccountPlainDocument extends PlainDocument{
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            //возможна вставка только букв, цифр и символа №
            char[] chars = str.toCharArray();

            boolean notLetterOrDigit = false; //не буква, не число и не символа №

            for(char x : chars){
                if (!Character.isLetterOrDigit(x) && !new Character(x).equals('№') && !new Character(x).equals(' ')){
                    notLetterOrDigit = true;
                    break;
                }
            }
            if (!notLetterOrDigit){
                super.insertString(offs, str, a);
            }
        }
    }
}
