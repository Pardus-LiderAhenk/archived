package tr.org.pardus.mys.liderahenksetup.main;

import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class SetupTableItem {
	
	private String description;
	private Image image;
	private IOnClickListener listener;

	public SetupTableItem(String description, Image image, IOnClickListener listener) {
		this.description = description;
		this.image = image;
		this.listener = listener;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public IOnClickListener getListener() {
		return listener;
	}

	public void setListener(IOnClickListener listener) {
		this.listener = listener;
	}

}
