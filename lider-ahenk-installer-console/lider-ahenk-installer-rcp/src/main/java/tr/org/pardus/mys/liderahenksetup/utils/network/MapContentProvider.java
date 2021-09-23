package tr.org.pardus.mys.liderahenksetup.utils.network;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.eclipse.jface.viewers.ArrayContentProvider;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class MapContentProvider extends ArrayContentProvider {
	
    /**
     * Returns the elements in the input, which must be either an array or a
     * <code>Collection</code>. 
     */
    @SuppressWarnings("rawtypes")
	@Override
	public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
        if (inputElement instanceof LinkedHashMap) {
        	return ((LinkedHashMap) inputElement).values().toArray();
        }
        if (inputElement instanceof Collection) {
			return ((Collection) inputElement).toArray();
		}
        return new Object[0];
    }

}
