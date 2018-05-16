package net.sourcedestination.sai.weblab.reporting;

import java.util.Map;

/** interface for a class that processes log results for a report */
public interface Processor {

    /** returns a regular-expression to match against log messages
     *
     * This expression should use memory parentheses for any components
     * of the message that need to be processed (rather than parsing them
     * out manually later).
     */
    public String getPattern();

    /** processes a log message parsed by the regular expression from getPattern
     *
     * Each of the group values from the memory parentheses will be passed in
     * as String arguments in order.
     * @param groups
     */
    public void processLogMessage(String ... groups);

    /** Updates the model with any data needed for the view.
     *
     * Only called after all log entries have been processed, but may be called
     * repeatedly.
     * @param model model used to render the complete report page
     */
    public void updateModel(Map<String, Object> model);
    
}