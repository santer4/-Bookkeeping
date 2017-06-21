import javax.swing.*;
import javax.swing.text.*;
import java.util.Locale;

public class MoneyTextField extends JTextField {
    private int valueFraction;
    private String textInField = "";
    private String txtNumber = "";
    private StringBuilder sb = new StringBuilder();

    private int afterPoint;
    private boolean havePoint;
    private int indexPoint;
    private int caretPosition;
    private int countEntireDigit;

    public MoneyTextField(int valueFraction){
        super();
        this.valueFraction = valueFraction;
    }


    @Override
    protected Document createDefaultModel() {
        return new MyPlainDocument();
    }

    private class MyPlainDocument extends PlainDocument {
        @Override
        public void setDocumentFilter(DocumentFilter filter) {
            super.setDocumentFilter(filter);
        }

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

            if (str == null || str.length() > 14 || countEntireDigit > 14) {
                return;
            }

            str = str.replace(",", ".");
            char[] chars = textInField.toCharArray();
            int countSeparator = 0;
            for (int i = 0; i < offs; i++) {
                if (chars[i] == 160) countSeparator++;
            }
            caretPosition = textInField.length() - offs;
            offs = offs - countSeparator; //для точной вставки в строку представляющую число

            String frmtNumberText;

            if (str.matches("\\d{2,}")
                    || str.matches("\\d*\\.\\d{1,2}")
                    || str.matches("\\d+\\.")
                    || ((valueFraction == 3) && str.matches("\\d*\\.\\d{3}"))) {
                //если вставляемая строка это две и более цифры
                //или десятичное чило с дробной частью не более 2-х цифр или строка типа ".12" или типа "123."


                sb.append(str);

                frmtNumberText = toFrmtNumberText(sb);
                update(frmtNumberText, a);
                MoneyTextField.this.setCaretPosition(0);

                return;
            }

            if ((str.matches("\\.") && !havePoint && txtNumber.length() - offs < valueFraction + 1)
                    || (str.matches("\\d") && !havePoint)
                    || (str.matches("\\d") && havePoint && offs <= indexPoint)
                    || (str.matches("\\d") && havePoint && offs > indexPoint && afterPoint < valueFraction)) {
                //если вставляемый символ точка или запятая и еще нет разделителя целой и дробной части
                //и после вставки точки дробная часть будет состоять из чисел в количестве valueFraction
                //если вставляемый символ это цифра и нет еще точки
                //если вставляемый символ это цифра, есть точка, но цифра вставляется до точки
                //если вставляемый символ это цифра, есть точка и количество знаков после точки менее valueFraction, но цифра
                //вставляется после точки
                sb.append(txtNumber);
                sb.insert(offs, str);

                frmtNumberText = toFrmtNumberText(sb);
                update(frmtNumberText, a);
                setCaretPosition(caretPosition);
            }
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {

            char isSeparator = textInField.charAt(offs);
            if (isSeparator == 160 && len == 1) {
                MoneyTextField.this.setCaretPosition(offs + 1);
                return;
            }

            caretPosition = textInField.length() - (offs + len);

            super.remove(offs, len);
            textInField = MoneyTextField.this.getText();
            txtNumber = textInField.replace(",", ".");
            txtNumber = txtNumber.replace(new String(new char[]{160}), "");

            sb.append(txtNumber);
            String frmtNumberText = toFrmtNumberText(sb);

            update(frmtNumberText, null);
            setCaretPosition(caretPosition);
        }

        public void update(String formatString, AttributeSet a) throws BadLocationException {

            super.remove(0, textInField.length());
            super.insertString(0, formatString, a);
            textInField = formatString;
            txtNumber = textInField.replace(new String(new char[]{160}), "");
            txtNumber = txtNumber.replace(",", ".");
            indexPoint = txtNumber.indexOf(".");
            sb.delete(0, sb.toString().length());

            if (havePoint) {
                countEntireDigit = indexPoint;
            } else {
                countEntireDigit = txtNumber.length();
            }

        }

        public String toFrmtNumberText(StringBuilder sb) {

            txtNumber = sb.toString();
            havePoint = txtNumber.contains(".");

            if (txtNumber.equals("")) {
                return "";
            }

            String formatString;
            if (txtNumber.matches("\\.\\d*")) {
                formatString = txtNumber.replaceFirst("\\.", ",");
                return formatString;
            }

            havePoint = txtNumber.contains(".");
            afterPoint = sb.length() - sb.indexOf(".") - 1;

            boolean startZero = false;
            if (txtNumber.startsWith("0")) {
                txtNumber = txtNumber.replaceFirst("0", "1");
                startZero = true;
            }

            Double numberDouble = Double.parseDouble(txtNumber);
            formatString = String.format(Locale.FRANCE, "%,.3f", numberDouble);

            if (havePoint) {
                formatString = formatString.substring(0, formatString.length() - (3 - afterPoint));
            } else {
                formatString = formatString.substring(0, formatString.length() - 4);
            }

            if (startZero) {
                formatString = formatString.replaceFirst("1", "0");
            }
            return formatString;
        }

        public void setCaretPosition(int oldPosition) {
            caretPosition = textInField.length() - oldPosition;
            if (caretPosition < 0) {
                MoneyTextField.this.setCaretPosition(0);
            } else {
                MoneyTextField.this.setCaretPosition(caretPosition);
            }
        }

    }
}
