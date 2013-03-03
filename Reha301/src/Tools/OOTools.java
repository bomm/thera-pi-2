package Tools;



import reha301.Reha301;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.document.IDocumentService;
import ag.ion.bion.officelayer.text.ITextDocument;
import ag.ion.noa.NOAException;

public class OOTools{
	public OOTools(){
		
	}
	/*******************************************************************************************/
	public static ITextDocument starteLeerenWriter(boolean visible){
		ITextDocument textDocument = null;
		try {
			if(!Reha301.officeapplication.isActive()){
				Reha301.starteOfficeApplication();
			}
			DocumentDescriptor docdescript = DocumentDescriptor.DEFAULT;
			docdescript.setHidden(!visible);
			IDocumentService documentService = Reha301.officeapplication.getDocumentService();
			IDocument document = documentService.constructNewDocument(IDocument.WRITER,docdescript );
			textDocument = (ITextDocument)document;
			if(visible){
				CommonTools.OOTools.inDenVordergrund(textDocument);
				textDocument.getFrame().setFocus();
			}
		} 
		catch (OfficeApplicationException exception) {
			exception.printStackTrace();
		} 
		catch (NOAException exception) {
			exception.printStackTrace();
		}
		return textDocument;
	}
}
	
