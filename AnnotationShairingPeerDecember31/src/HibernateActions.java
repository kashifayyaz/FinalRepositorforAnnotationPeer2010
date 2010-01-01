/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kashif
 */
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.search.Search;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;

public class HibernateActions {

    public static FullTextSession ftsess;
    public SignatureTest signaturetest;
    public KeyPairGenerator kpg;
    public KeyPair kp;
    private PrivateKey prvk;
    public PublicKey pubk;
    RDFHandeling rdf = new RDFHandeling();
    String[] fields = new String[]{"resourceURL", "description", "creationTime", "annotationAuthor", "semToAnn"};

    SessionHandler sh = new SessionHandler();
    Session session = null;
    Transaction tx = null;


    HibernateActions() throws NoSuchAlgorithmException, Exception{

    kpg = KeyPairGenerator.getInstance("DSA");
    kpg.initialize(512); // 512 is the keysize.
    kp = kpg.generateKeyPair();
    prvk = kp.getPrivate();
    pubk = kp.getPublic();
    session = sh.getSession();
    ftsess = indexing(session);

    }

    public void setUpDB(){
         //Set up database tables

        HibernateUtil.droptable("drop table annotation");
        HibernateUtil.droptable("drop table annotation_child");
        HibernateUtil.setup("create table annotation ( AnnID VARCHAR(50) primary key not null, ResourceURL VARCHAR(40) not null, Description VARCHAR(50), CreationTime TIMESTAMP, AnnotationAuthor VARCHAR(30) not null, SemToAnn varchar(40), FileLocation varchar(30) not null, DigitalSignatures blob not null)");
        HibernateUtil.setup("create table annotation_child (AnnID VARCHAR(50),Query_Term varchar(30),Text varchar(30),Concept  varchar(30),id int)");

    }
    public void populateBothTables(LinkedList list) throws Exception{
        try {
            //session = sh.getSession();//it is invoked up in constructor
            tx = session.beginTransaction();


            String struuid = list.get(0).toString();
            String annAuthor = list.get(1).toString();
            System.out.println(list.get(2).toString());

            Date creationTime = getTime();

            String semToAnn = list.get(3).toString();
            String resourceURL = "";
            String description = "";
            String fileLoc = "";


            String[] signatures = {struuid, resourceURL, description, creationTime.toString(), annAuthor, semToAnn, fileLoc};
            Blob blob = getDigSig(signatures);

            String query = list.get(5).toString();
            String concept = list.get(6).toString();
            String text = list.get(7).toString();
            Random generator = new Random();
            int r = generator.nextInt();

            Annotation a1 = new Annotation();
            a1.setAnnID(struuid);
            a1.setResourceURL(resourceURL);
            a1.setDescription(description);
            a1.setCreationTime(creationTime);
            a1.setAnnotationAuthor(annAuthor);
            a1.setSemToAnn(semToAnn);
            a1.setFileLocation(fileLoc);
            a1.setDigitalSignatures(blob);

            a1.setQueryData(new HashSet());

            a1.getQueryData().add(new AnnotationChild(struuid,r,query, text, concept));

            session.save(a1);
            tx.commit();
            JOptionPane.showMessageDialog(null, "Data has been read and saved in DB");
        } catch ( HibernateException e ) {
            if ( tx != null ) tx.rollback();
            e.printStackTrace();
        }
    }
    public void populateAnnotation_Child(LinkedList uuid, LinkedList list) throws Exception{
            System.out.println("size of list for uuids is "+ uuid.size());
            for(int i = 0; i <uuid.size(); i++){
            tx = session.beginTransaction();
            Random generator = new Random();
            int r = generator.nextInt();
             AnnotationChild childa1 = new AnnotationChild();
            childa1.setChildId(r);
            childa1.setLocalAnnId(uuid.get(i).toString());
            childa1.setQueryTerm(list.get(0).toString());
            childa1.setConcept(list.get(1).toString());
            childa1.setText(list.get(2).toString());
            session.save(childa1);
            tx.commit();
           }
    }
     public void populateTables(LinkedList list) throws Exception{
             try {
            //session = sh.getSession();//it is invoked up in constructor
            tx = session.beginTransaction();

            // Create an Annotation object and save it
            SourceofUUID.UUID uuid = new SourceofUUID.UUID();
            String struuid = uuid.toString();
            
            String resourceURL = list.get(0).toString();//"www.2ndinterface.com";
            String description = list.get(1).toString();//"second interface description for December";
            Date creationTime = getTime();
            System.out.println(creationTime.toString());
            String annAuthor = list.get(2).toString();//"2ndinterface@yahoo.com";
            String semToAnn = list.get(3).toString();//"www.2sgd.pk";

            String fileLoc = "c://fkdskfs";

            
            Annotation a1 = new Annotation();
            a1.setAnnID(struuid);
            a1.setResourceURL(resourceURL);
            a1.setDescription(description);
            a1.setCreationTime(creationTime);
            a1.setAnnotationAuthor(annAuthor);
            a1.setSemToAnn(semToAnn);
            a1.setFileLocation(fileLoc);
/////////////////// Digital Signature Process///////////////////////////////

            String[] signatures = {a1.getAnnID(), a1.getResourceURL(), a1.getDescription(), a1.getCreationTime().toString(), a1.getAnnotationAuthor(), a1.getSemToAnn(), a1.getFileLocation()};
            Blob blob = getDigSig(signatures);
            a1.setDigitalSignatures(blob);



            Blob testblob = a1.getDigitalSignatures();
            String cnvDS = convertDStoBase64(testblob);

/////////// populating second table///////////////////////////
            
            session.save(a1);
            tx.commit();
            
            //RDFHandeling.createRDF(struuid, annAuthor, creationTime, cnvDS, semToAnn, ids, query, text, concept);
            //ftsess = indexing(session);// already invoked in constructor
            verifyDigSig(testblob, signatures);


          //analyze(fields);

            //System.exit(0);
        } catch ( HibernateException e ) {
            if ( tx != null ) tx.rollback();
            e.printStackTrace();
        } finally {
            //session.close();

        }
        
    }
     public void quit(){
         session.close();
         System.exit(0);
     }
    public void verifyDigSig(Blob testblob, String[] signatures) throws SQLException, Exception{
            byte[] test = testblob.getBytes(1, (int)testblob.length());
            boolean result = SignatureTest.verify(signatures, pubk, "SHAwithDSA", test);
            System.out.println("Signature Test ***********" + result);

    }
    public Blob getDigSig(String[] signatures) throws Exception{
        signaturetest = new SignatureTest();
            byte[] sigbytes = SignatureTest.sign(signatures, prvk, "SHAwithDSA");
            //String bytestr = new String(sigbytes);
            //System.out.println("DS going are: " + bytestr);
            Blob blob = new SerialBlob(sigbytes);
            return blob;
            
    }
    public static String convertDStoBase64(Blob ob) throws SQLException{
            Base64 bs = new Base64();
            byte[] test = ob.getBytes(1, (int)ob.length());
            byte[] dsbyte = bs.decode(test);
            String dsstr = new String(dsbyte);
            return dsstr;
   
    }
     public static Date getTime(){
            Calendar cal = Calendar.getInstance();
            Date datetime = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //creationTime = sdf.format(cal.getTime());
            datetime = cal.getTime();
            return datetime;
     }
     public static FullTextSession indexing(Session session){
         /////////////////////////////////////////////indexing////////////////////////

    List<Annotation> notes = session.createCriteria(Annotation.class).list();
    ftsess = Search.getFullTextSession(session);

    ftsess.getTransaction().begin();
      for (Annotation annot : notes) {
              ftsess.index(annot);
    }
    ftsess.getTransaction().commit();
    return ftsess;
    }

    public List analyzeSingleField(String fields, String key) throws IOException, ParseException, SQLException {

            System.out.println("Analzying \"" + key + "\"");
            StandardAnalyzer analyzer = new StandardAnalyzer();
            TokenStream stream = analyzer.tokenStream("", new StringReader(key));
            QueryParser parser = new QueryParser(fields, new StandardAnalyzer());
            System.out.println("this is single field analyzer");
            org.apache.lucene.search.Query lquery = null;
            String test = null;
                org.apache.lucene.analysis.Token token = stream.next();
                System.out.println( token.termText());
                test = token.termText();
                lquery = parser.parse(test);
               List list = search(lquery);
               return list;
      }

    public List analyzeMultipleFields(String[] fields, String[] queries) throws IOException, ParseException, SQLException {

            Query query = MultiFieldQueryParser.parse( queries, fields, new StandardAnalyzer() );
			System.out.println( "Query came is "+query.toString() );
            List list = search(query);
            return list;
//            System.out.println("now going for wild card query");
//            String userInput = "k*ashi?";
//            WildcardQuery wildCardQuery = new WildcardQuery( new Term( "annotationAuthor", userInput ) );

    }
    public List analyzeRangeQuery(String field, String[] rquery) throws ParseException, SQLException{
//         String[] fields = new String[1];
//        String[] q = new String[1];
//        fields[0] = field;
//        q[0] = rquery;
//        Query query = MultiFieldQueryParser.parse( q, fields, new StandardAnalyzer() );
//			System.out.println( "Query came is "+query.toString() );
//            List list = search(query);
//            return list;

        Term lower = new Term(field, rquery[0]);
        Term upper = new Term(field, rquery[1]);
        RangeQuery query = new RangeQuery(lower, upper, true);
        //ConstantScoreRangeQuery query = new ConstantScoreRangeQuery(field, rquery[0],rquery[1], true, true);
        System.out.println("Range Query is "+ query.toString());
        org.hibernate.search.FullTextQuery hibQuery = ftsess.createFullTextQuery(query, Annotation.class);
        List<Annotation> results = hibQuery.list();
        return results;
    }
    public List analyzeWildCardQuery(String field, String query1) throws SQLException{

            WildcardQuery query = new WildcardQuery( new Term( field, query1 ) );
			System.out.println( query.toString() );

			org.hibernate.search.FullTextQuery hibQuery = ftsess.createFullTextQuery( query, Annotation.class );
			List<Annotation> results = hibQuery.list();
            
            return results;
    }
    public List analyzePhraseQuery(String field, String query1) throws SQLException{

           StringTokenizer st = new StringTokenizer( query1, " " );

			PhraseQuery query = new PhraseQuery();
			while (st.hasMoreTokens()) {
				query.add( new Term( field, st.nextToken() ) );
			}
            System.out.println("Phrase Query is  "+ query.toString() );
			org.hibernate.search.FullTextQuery hibQuery = ftsess.createFullTextQuery( query, Annotation.class );
			List<Annotation> results = hibQuery.list();
			return results;
    }
    public List analyzePrefixQuery(String field, String query1) throws SQLException{
        PrefixQuery query = new PrefixQuery( new Term( field, query1 ) );
		System.out.println("Prefix query is "+ query.toString() );

		org.hibernate.search.FullTextQuery hibQuery = ftsess.createFullTextQuery( query, Annotation.class );
		List<Annotation> results = hibQuery.list();
        return results;
   }
   public List analyzeTermQuery(String field, String query1) throws SQLException{
       Term term = new Term( field, query1 );
       TermQuery query = new TermQuery( term );
     	System.out.println("term Query is " +query.toString() );

        org.hibernate.search.FullTextQuery hibQuery = ftsess.createFullTextQuery( query, Annotation.class );
		List<Annotation> results = hibQuery.list();
        return results;

   }


      ///////////////////////////////////////////////////////////////////////////
    // create the Hibernate search query as a wrapper around
    // the Lucene query

    public static List<Annotation> search(org.apache.lucene.search.Query lquery ) throws SQLException{
         org.hibernate.search.FullTextQuery hquery = ftsess.createFullTextQuery(lquery, Annotation.class);
         System.out.println("search called");
    List results = hquery.list();  // execute the query
    return results;
    }

}
