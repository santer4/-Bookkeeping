import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Alexandr on 15.04.2017.
 */
//класс для поля "Количество" и для поля "Сумма"
public class QuantityTextField extends JTextField{

    private String textField;
    private int lengthTextField;
    private int indexPoint = -1;
    private  boolean havePoint = false;
    private int countDigit = 0;
    QuantityTextField field = this;
    private int valueFraction = 3; //количество цифр после запятой (для суммы =2, для количества =3)
    public QuantityTextField(int valueFraction){
        super();
        this.valueFraction = valueFraction;
        //Listener для событий в документе текствового поля (удаления, добавления символов)
        getDocument().addDocumentListener(new QuantityDocumentListener());
    }
    public QuantityTextField(int valueFraction, int columns){
        super(columns);
        this.valueFraction = valueFraction;
        getDocument().addDocumentListener(new QuantityDocumentListener());
    }

    @Override
    protected Document createDefaultModel() {
        return new QuantityPlainDocument();
    }

    //документ для текстового поля
    private class QuantityPlainDocument extends PlainDocument{
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }
            str = str.replace(",",".");
            char[] insertChar = str.toCharArray();
            if (insertChar.length == 1){
                //если вставляемая строка это один символ
                if (new Character(insertChar[0]).equals('.')  && !havePoint && lengthTextField - offs < valueFraction + 1) {
                    //если вставляемый символ точка и еще нет разделителя целой и дробной части
                    //и после вставки точки дробная часть будет состоять из 3-х чисел
                    super.insertString(offs, new String(insertChar),a);
                    havePoint = true;
                    return;
                }
                if (Character.isDigit(insertChar[0]) && !havePoint){
                    //если вставляемый символ это цифра и нет еще точки
                    super.insertString(offs, new String(insertChar), a);
                    return;
                }
                if (Character.isDigit(insertChar[0]) && havePoint && offs <= indexPoint){
                    //если вставляемый символ это цифра, есть точка, но цифра вставляется до точки
                    super.insertString(offs, new String(insertChar), a);
                    return;
                }
                if (Character.isDigit(insertChar[0]) && havePoint && offs > indexPoint && countDigit < valueFraction) {
                    //если вставляемый символ это цифра, есть точка и количество знаков после точки менее трех
                    countDigit++;// после вставки цифры увеличить количество цифр после запятой
                    super.insertString(offs, new String(insertChar),a);
                    return;
                }
            }

            if (str.matches("\\d{2,}")){
                //если вставляемая строка это две и более цифры
                super.remove(0,lengthTextField);//удалить содержимое документа
                super.insertString(0, str, a);
                return;
            }

            //int lengthStr = str.length();
            //int indexDelimiter = str.indexOf(".");

            if ((valueFraction == 2) && str.matches("\\d*\\.\\d{1,2}")){
                //если вставляемая строка это десятичное чило с дробной частью не более 3-х цифр или строка типа ".123" или типа "123."
                super.remove(0, lengthTextField);//удалить содержимое документа
                super.insertString(0, str, a);
                return;
            }

            if ((valueFraction == 3) && str.matches("\\d*\\.\\d{1,3}")){
                //если вставляемая строка это десятичное чило с дробной частью не более 3-х цифр или строка типа ".123" или типа "123."
                super.remove(0, lengthTextField);//удалить содержимое документа
                super.insertString(0, str, a);
            }
        }
    }

    private class QuantityDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

        public void update(){
            textField = field.getText();
            lengthTextField = textField.length();

            if(!textField.contains(".")){
                //если точки нет
                indexPoint = -1;
                havePoint = false;
            } else {
                //если точка есть, найти индекс точки и количество знаков после запятой
                indexPoint = textField.indexOf(".");
                havePoint = true;
                countDigit = lengthTextField - indexPoint - 1;
            }
        }
    }
}
