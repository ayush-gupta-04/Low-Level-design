import java.util.Stack;

// 1. MEMENTO: The snapshot (Immutable lockbox)
class TextMemento {
    private final String state;

    public TextMemento(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}

// 2. ORIGINATOR: The text editor we are using
class TextEditor {
    private StringBuilder content;

    public TextEditor() {
        this.content = new StringBuilder();
    }

    public void write(String text) {
        content.append(text);
    }

    public String print() {
        return content.toString();
    }

    // Creates a snapshot of the current text
    public TextMemento save() {
        return new TextMemento(content.toString());
    }

    // Restores the text from a snapshot
    public void restore(TextMemento memento) {
        this.content = new StringBuilder(memento.getState());
    }
}

// 3. CARETAKER: Manages our Undo history
class HistoryManager {
    private final Stack<TextMemento> history = new Stack<>();

    public void save(TextEditor editor) {
        history.push(editor.save());
    }

    public void undo(TextEditor editor) {
        if (!history.isEmpty()) {
            editor.restore(history.pop());
        } else {
            System.out.println("Nothing to undo! 🤷‍♂️");
        }
    }
}

// 🎮 MAIN CLASS: Let's test it out!
public class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        HistoryManager history = new HistoryManager();

        // Type some text and save state
        editor.write("Hello! ");
        history.save(editor); // Save 1

        editor.write("This is a tutorial. ");
        history.save(editor); // Save 2

        editor.write("Oops, a typo!"); 
        
        System.out.println("Current Text: " + editor.print()); 
        // Output: Hello! This is a tutorial. Oops, a typo!

        // Let's Undo!
        history.undo(editor);
        System.out.println("After 1st Undo: " + editor.print()); 
        // Output: Hello! This is a tutorial. 

        // Undo again!
        history.undo(editor);
        System.out.println("After 2nd Undo: " + editor.print()); 
        // Output: Hello! 
    }
}
