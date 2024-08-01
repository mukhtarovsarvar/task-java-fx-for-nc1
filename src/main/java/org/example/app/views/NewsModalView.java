package org.example.app.views;

import org.example.jfxsupport.AbstractFxmlView;
import org.example.jfxsupport.FXMLView;
import org.springframework.context.annotation.Scope;

@FXMLView("/templates/news.fxml")
@Scope("prototype")
public class NewsModalView extends AbstractFxmlView {

}
