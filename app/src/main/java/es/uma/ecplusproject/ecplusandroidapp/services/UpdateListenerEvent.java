package es.uma.ecplusproject.ecplusandroidapp.services;

/**
 * Created by francis on 24/11/16.
 */
public class UpdateListenerEvent {
    public enum Action {START, STOP_DATABASE, STOP_FILE};
    public enum Element {SYNDROMES, WORDS};

    private Action action;
    private Element element;
    private boolean somethingChanged;

    protected UpdateListenerEvent(Action action, Element element, boolean somethingChanged) {
        this.action=action;
        this.element=element;
        this.somethingChanged=somethingChanged;
    }

    public Action getAction() {
        return action;
    }

    public Element getElement() {
        return element;
    }

    public boolean isSomethingChanged() {
        return somethingChanged;
    }

    protected static UpdateListenerEvent startUpdateWordsEvent() {
        return new UpdateListenerEvent(Action.START, Element.WORDS, false);
    }

    protected static UpdateListenerEvent stopUpdateWordsDatabaseEvent(boolean databaseChanged) {
        return new UpdateListenerEvent(Action.STOP_DATABASE, Element.WORDS, databaseChanged);
    }

    protected static UpdateListenerEvent stopUpdateWordsFilesEvent(boolean filesChanged) {
        return new UpdateListenerEvent(Action.STOP_FILE, Element.WORDS, filesChanged);
    }

    protected static UpdateListenerEvent startUpdateSyndromesEvent() {
        return new UpdateListenerEvent(Action.START, Element.SYNDROMES, false);
    }

    protected static UpdateListenerEvent stopUpdateSyndromesEvent(boolean databaseChanged) {
        return new UpdateListenerEvent(Action.STOP_DATABASE, Element.SYNDROMES, databaseChanged);
    }

}
