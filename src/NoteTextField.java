import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


//текстовое поле для добавления заметки
public class NoteTextField extends JTextField {

    public NoteTextField(){
        super();
    }

    @Override
    protected Document createDefaultModel() {
        return new NameAccountPlainDocument();
    }

    private class NameAccountPlainDocument extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            //возможна вставка любых символов кроме /
            char[] chars = str.toCharArray();

            boolean haveSlash = false; //в слове есть слэш, если haveSlash = true

            for(char x : chars){
                if (new Character(x).equals('/')){
                    haveSlash = true;
                    break;
                }
            }
            if (!haveSlash){
                super.insertString(offs, str, a);
            }
        }
    }
}
