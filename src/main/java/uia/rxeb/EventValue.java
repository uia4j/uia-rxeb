package uia.rxeb;

public interface EventValue {

    public static final EventValue UNKNOWN = new EventValue() {

        @Override
        public String toString() {
            return "Unknown";
        }
    };
}
