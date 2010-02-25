package utils;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERTags;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;

public class PKCSIn {
	public static void main(String[] args) {
        try{
        	
        PEMReader pemReader = new PEMReader( new InputStreamReader(new FileInputStream("C:/Nebraska/" + "510841109.pem")));

         PKCS10CertificationRequest certRequest = (PKCS10CertificationRequest)pemReader.readObject();
  //       System.out.println(new String(certRequest.getDEREncoded()));
         System.out.println(certRequest.getCertificationRequestInfo());
         CertificationRequestInfo info = certRequest.getCertificationRequestInfo();
//         System.out.println("Aus Pem "+info.getDERObject());
         ASN1Encodable aenc = info.getDERObject();
         System.out.println(new String(aenc.getDEREncoded()));
         ASN1InputStream ain =

        	 new ASN1InputStream(new FileInputStream("C:/Nebraska/" + "510841109.p10"));
         //ain.readObject();
         
         DERObject derob = ain.readObject();
         //System.out.println("nur DERObject"+new String(derob.getDEREncoded()));
         //System.out.println("DERObject getDEREncoded = "+new String(derob.getDEREncoded()));
         System.out.println("DERObject getDEREncoded = "+derob.getDERObject());
         DERObject derob2 = derob.getDERObject();
         System.out.println(DERTags.TAGGED);
         System.out.println(DERTags.GENERAL_STRING);
         System.out.println(DERTags.OBJECT_IDENTIFIER);
         System.out.println("derob2"+derob2);
         
         ASN1Object aobj = (ASN1Object) derob2.toASN1Object();
         System.out.println("aobj"+aobj);
         
         aobj.getDEREncoded();

        // Attribute attr = Attribute.getInstance(aobj);
         //ASN1TaggedObject atag = ASN1TaggedObject.getInstance(aobj);
         //System.out.println("attr="+attr);
         //System.out.println("attr.getValues="+attr.getAttrValues());
         System.out.println("DERObject = "+derob);
         //Collection<DERObject> col = (Collection)derob;//(Collection)derob;
       
         //ASN1Set set = ASN1Set.getInstance(derob.OCTET_STRING);
         ASN1EncodableVector dvec = new ASN1EncodableVector();
         dvec.add(derob);
         BERSet bset = new BERSet(dvec);
         System.out.println(bset.getObjectAt(0));
         
         System.out.println(dvec);
         System.out.println(dvec.get(0));
         Attribute attr;
         //attr = Attribute.getInstance(derob.BER);
         //System.out.println(derob.BER);
         //System.out.println( new String(attrib.getDEREncoded()));
        // Get attributes :
        // extract the extension request attribute
        ASN1Set attributes = certRequest.getCertificationRequestInfo().getAttributes();
        
        for (int i = 0; i != attributes.size(); i++){
            attr = Attribute.getInstance(attributes.getObjectAt(i));
             // process extension request
             if (attr.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest))
             {
                X509Extensions extensions = X509Extensions.getInstance(attr.getAttrValues().getObjectAt(0));

                // unfortunately method oids() does not return Enumeration<DERObjectIdentifier>
                Enumeration<?> e = extensions.oids();
                while (e.hasMoreElements())
                {

                    DERObjectIdentifier oid = (DERObjectIdentifier)e.nextElement();
                    X509Extension       extVal = extensions.getExtension(oid);

                    ASN1InputStream     aIn = new ASN1InputStream(extVal.getValue().getEncoded());

                    System.out.println(ASN1Dump.dumpAsString(aIn.readObject()));

                }
             }
          }
 }catch(Exception e)
 {
     e.printStackTrace();
 }
} 	

}
