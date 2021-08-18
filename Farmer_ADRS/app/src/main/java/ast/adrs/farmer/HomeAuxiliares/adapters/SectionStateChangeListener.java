package ast.adrs.farmer.HomeAuxiliares.adapters;


public interface SectionStateChangeListener {
    void onSectionStateChanged(Section section, boolean isOpen);
}