/* Interface Gráfica para Organizadores
 * Igor Nascimento dos Santos
 * Criado em 13-12-2014
 * Modificado pela última vez em 21-12-1014
 */

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;

public class InterfaceGrafica extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextField nomeRegistro, matriculaRegistro, emailRegistro, enderecoRegistro, sexoRegistro,
    idadeRegistro;
    private JButton adicionaRegistro, apagaRegistro, retornaRegistro, botaoPesquisar,
    botaoAdicionar, botaoRemover, botaoVoltar;
    private ManipuladorEventos manipulador;
    private JPanel painelCentro, painelNorte;
    private IFileOrganizer org;
    private JMenuBar jmb;
    private JMenu jmArquivo, jmAjuda;  
    private JMenuItem jmSair, jmCreditos, jmHelp, jmAbrir;  
    
    public InterfaceGrafica () {
        setTitle("Gerenciador de Arquivos");  	
        initUI();
    	setSize (600,400);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setVisible(true);   
    }
    public void initUI() {
        // Procedimentos iniciais
    	try {
			org = new OrganizadorSequencial("default.db");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
        // Inicializacao dos componentes visuais
        ImageIcon imagemTituloJanela = new ImageIcon("icons/GerReg.png");  
        setIconImage(imagemTituloJanela.getImage());  
        botaoVoltar = new JButton("Voltar");
        botaoVoltar.setName("voltar");
        botaoAdicionar = new JButton("Adicionar");
        botaoAdicionar.setName("adiciona");
        botaoRemover = new JButton("Remover");
        botaoRemover.setName("remove");
        botaoPesquisar = new JButton ("Pesquisar");
        botaoPesquisar.setName("pesquisa");
        nomeRegistro = new JTextField (48);
        nomeRegistro.setName("nomeReg");
        idadeRegistro = new JTextField (23);
        idadeRegistro.setName("idadeReg");
        sexoRegistro = new JTextField (21);
        sexoRegistro.setName("sexoReg");
        emailRegistro = new JTextField (23);
        emailRegistro.setName("emailReg");
        enderecoRegistro = new JTextField (46);
        enderecoRegistro.setName("enderecoReg");
        matriculaRegistro = new JTextField (18);
        matriculaRegistro.setName("matriculaReg");
        adicionaRegistro = new JButton ("Adicionar");
        adicionaRegistro.setName("addReg");
        apagaRegistro = new JButton ("Remover");
        apagaRegistro.setName("removeReg");
        retornaRegistro = new JButton ("Ver");
        retornaRegistro.setName("retornaReg");
        jmb = new JMenuBar();
        jmArquivo = new JMenu("Arquivo");
        jmAjuda = new JMenu("Ajuda");
        jmAbrir = new JMenuItem("Abrir", new ImageIcon("icons/open.png"));
        jmSair = new JMenuItem("Sair", new ImageIcon("icons/exit.png"));
        jmCreditos = new JMenuItem("Sobre", new ImageIcon("icons/help-about.png"));
        jmHelp = new JMenuItem("Ajuda" ,new ImageIcon("icons/help-contents.png"));
        setJMenuBar (jmb);
        jmb.add(jmArquivo);
        jmb.add(jmAjuda);
        jmArquivo.add(jmAbrir);
        jmArquivo.add(jmSair);
        jmAjuda.add(jmHelp);
        jmAjuda.add(jmCreditos);
        // Colocacao dos componentes no container
        setLayout (new BorderLayout());
        painelNorte = new JPanel();
        painelNorte.add(new JLabel ("Escolha a operação desejada:"));
        add(painelNorte,BorderLayout.NORTH);
        painelCentro = new JPanel();
        painelCentro.add(adicionaRegistro);
        painelCentro.add(apagaRegistro);
        painelCentro.add(retornaRegistro);
        add(painelCentro,BorderLayout.CENTER);
        // Tratamento de eventos
        manipulador = new ManipuladorEventos();
        adicionaRegistro.addActionListener (manipulador);
        apagaRegistro.addActionListener (manipulador);
        retornaRegistro.addActionListener (manipulador);
        botaoPesquisar.addActionListener (manipulador);
        botaoAdicionar.addActionListener (manipulador);
        botaoRemover.addActionListener(manipulador);
        botaoVoltar.addActionListener(manipulador);
        jmSair.addActionListener(manipulador);
        jmAjuda.addActionListener(manipulador);
        jmCreditos.addActionListener(manipulador);
        jmAbrir.addActionListener(manipulador);
        jmHelp.addActionListener(manipulador);
        // Procedimentos finais  
    }
    private void setValores(){
    	this.nomeRegistro.setText("");;
    	this.matriculaRegistro.setText("");
    	this.emailRegistro.setText("");
    	this.enderecoRegistro.setText("");
    	this.sexoRegistro.setText("");
    	this.idadeRegistro.setText("");
    }
    // Método que extrai os atributos dos campos de texto
    private void extrairAtributos(Aluno a, int num) throws IOException{
    	ByteBuffer buf = a.getBuffer();
    	String nome, endereco, email, sexo;
    	int matricula;
    	short idade;
    	matricula = buf.getInt();
    	byte[] b_nome = new byte[50];
    	buf.get(b_nome);
    	nome = new String(b_nome);
    	byte[] b_endereco = new byte[60];
    	buf.get(b_endereco);
    	endereco = new String(b_endereco);
    	idade = buf.getShort();
    	byte[] b_sexo = new byte[1];
    	buf.get(b_sexo);
    	sexo = new String(b_sexo);
    	byte[] b_email = new byte[40];
    	buf.get(b_email);
    	email = new String(b_email);
        painelNorte.removeAll();
        if(num == 0) painelNorte.add(new JLabel ("Estes são os dados do registro selecionado:"));
        else if(num ==1) painelNorte.add(new JLabel ("Dados do registro removido:"));
        painelNorte.validate();
        painelNorte.repaint();
        painelCentro.removeAll();
        JLabel nomeLabel = new JLabel ("Nome: " + nome +"\n");
        nomeLabel.setName("nomeLabel");
        JLabel matriculaLabel = new JLabel ("Matrícula: " + matricula + "\n");
        matriculaLabel.setName("matriculaLabel");
        JLabel emailLabel = new JLabel ("Email: " + email + "\n");
        emailLabel.setName("emailLabel");
        JLabel enderecoLabel = new JLabel ("Endereço: " + endereco + "\n");
        enderecoLabel.setName("enderecoLabel");
        JLabel idadeLabel = new JLabel ("Idade: " + idade +  "\n");
        idadeLabel.setName("idadeLabel");
        JLabel sexoLabel = new JLabel ("Sexo: " + sexo + "\n");
        sexoLabel.setName("sexoLabel");
        painelCentro.add(nomeLabel);
        painelCentro.add(matriculaLabel);
        painelCentro.add(emailLabel);
        painelCentro.add(enderecoLabel);
        painelCentro.add(idadeLabel);
        painelCentro.add(sexoLabel);
        painelCentro.add(botaoVoltar);
        painelCentro.validate();
        painelCentro.repaint();
    }
    // Redirecionamentos
    private void redirecionarPagina (int num) {
    	painelNorte.removeAll();
    	painelCentro.removeAll();
    	if(num == 1){
    		painelNorte.add(new JLabel ("Digite os dados do usuário:"));
        	painelCentro.add(new JLabel ("Nome:"));
        	painelCentro.add(nomeRegistro);
        	painelCentro.add(new JLabel ("Matrícula:"));
        	painelCentro.add(matriculaRegistro);
        	painelCentro.add(new JLabel ("Email:"));
        	painelCentro.add(emailRegistro);
        	painelCentro.add(new JLabel ("Endereço:"));
        	painelCentro.add(enderecoRegistro);
        	painelCentro.add(new JLabel ("Idade:"));
        	painelCentro.add(idadeRegistro);
        	painelCentro.add(new JLabel ("Sexo:"));
        	painelCentro.add(sexoRegistro);
        	painelCentro.add(botaoAdicionar);
        	painelCentro.add(botaoVoltar);
        }
    	else if(num == 3){
        	painelNorte.add(new JLabel ("Digite a matrícula a ser removida:"));
            painelCentro.add(matriculaRegistro);
        	painelCentro.add(botaoRemover);
            painelCentro.add(botaoVoltar);
        }
    	else if(num == 2){
        	painelNorte.add(new JLabel ("Digite a matrícula a ser pesquisada:"));
            painelCentro.add(matriculaRegistro);
        	painelCentro.add(botaoPesquisar);
            painelCentro.add(botaoVoltar);
        }
    	else if(num == 0){
        	setValores();
        	painelNorte.add(new JLabel ("Escolha a sua operação:"));
            painelCentro.add(adicionaRegistro);
            painelCentro.add(apagaRegistro);
            painelCentro.add(retornaRegistro);
        }
    	else if(num == 4){
            painelNorte.add(new JLabel ("Gerenciador de Registros (ArGr)\n"));
            painelCentro.add(new JLabel ("Igor Nascimento dos Santos\n"));
            painelCentro.add(botaoVoltar);
        }
    	else if(num == 5){
        	JFileChooser fileChooser = new JFileChooser();
        	fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        	painelCentro.add(fileChooser);
        	int result = fileChooser.showOpenDialog(this);
        	try{
        		if(result == JFileChooser.CANCEL_OPTION) redirecionarPagina(0);
        		File name = fileChooser.getSelectedFile();
        		String nome = name.getName();
        		System.out.println(nome);
        		org = new OrganizadorSequencial(nome);
        		redirecionarPagina(0);
        	}
        	catch(Exception e){
        	}
        }
        painelNorte.validate();
    	painelNorte.repaint();
        painelCentro.validate();
        painelCentro.repaint();
    }
    
    // Classe interna que manipula os eventos
    private class ManipuladorEventos implements ActionListener {
        public void actionPerformed (ActionEvent e){
            if (e.getSource() == adicionaRegistro) {
            	redirecionarPagina(1);
            }
            else if (e.getSource() == apagaRegistro) {
            	redirecionarPagina(3);	
            }
            else if (e.getSource() == retornaRegistro) {              
            	redirecionarPagina(2);
            }
            else if (e.getSource() == botaoAdicionar) {
        		String nome, endereco, email, sexo;
        		int matricula;
        		short idade;
        		try{
            		nome = nomeRegistro.getText();
            		endereco = enderecoRegistro.getText();
            		email = emailRegistro.getText();
            		idade = (short) Integer.parseInt(idadeRegistro.getText());
            		matricula = Integer.parseInt(matriculaRegistro.getText());
            		sexo = sexoRegistro.getText();
        			Aluno a = new Aluno(matricula, nome, endereco, idade, sexo, email);
        			org.addReg(a);
        			JOptionPane.showMessageDialog(null, "Registro adicionado com sucesso!");
        			redirecionarPagina(0);
            	}
        		catch(Exception a){
        			JOptionPane.showMessageDialog(null, "Insira os valores corretamente!");
        		}
            }
            else if (e.getSource() == botaoRemover) {
            	try { 
            		int matricula = Integer.parseInt(matriculaRegistro.getText());
            		Aluno a = org.delReg(matricula);
            		if(a == null){
            			JOptionPane.showMessageDialog(null, "O registro com a chave informada não foi localizado!");
            			redirecionarPagina(3);
            		}
            		else{
            			JOptionPane.showMessageDialog(null, "Registro removido com sucesso!");
            			extrairAtributos(a,1);
            		}
            	}
            	catch(Exception b) {
            		JOptionPane.showMessageDialog(null, "Insira a matrícula !");
            	}
            }
            else if (e.getSource() == botaoPesquisar) {
            	try { 
            		int matricula = Integer.parseInt(matriculaRegistro.getText());
            		Aluno a = org.getReg(matricula);
            		if(a == null){
            			JOptionPane.showMessageDialog(null, "O registro com a chave informada não foi localizado!");
            			redirecionarPagina(2);
            		}
            		else extrairAtributos(a, 0);
            	}
            	catch(Exception b) {
            		JOptionPane.showMessageDialog(null, "Insira a matrícula !");
            	}
        	}
            else if (e.getSource() == botaoVoltar) {
        		redirecionarPagina(0);
        	}
            else if(e.getSource() == jmSair){
            	System.out.println("Obrigado por utilizar o ArGr !");
            	System.out.println(":) :)");
            	System.exit(0);
            }
            else if(e.getSource() == jmHelp){
                try {  
                   System.out.println("Abrindo o browser...");
                   // Este comando só roda em sistemas UNIX, como distribuições Linux e BSD's
                   Runtime.getRuntime().exec("xdg-open doc/Ajuda.html");  
                }  
                catch(IOException iOException){  
                   iOException.printStackTrace();  
                } 
            }
            else if(e.getSource() == jmCreditos){
            	redirecionarPagina(4);
            }
            else if(e.getSource() == jmAbrir){
            	redirecionarPagina(5);
            }
        }
    }
    public static void main (String args[]) throws IOException, Throwable{
        InterfaceGrafica i = new InterfaceGrafica();
        i.finalize();
    }
}