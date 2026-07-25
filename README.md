


<h1>🎬 ScreenTrack API</h1>

<p class="subtitle">
    API REST para gerenciamento de filmes e séries assistidos,
    inspirada em plataformas como Letterboxd.
</p>


<div class="card">

<h2>📌 Sobre o projeto</h2>

<p>
O ScreenTrack é uma API desenvolvida em Java utilizando Spring Boot,
com o objetivo de permitir que usuários registrem filmes e séries assistidos,
criem avaliações, organizem favoritos e acompanhem sua jornada audiovisual.
</p>

<p>
O projeto foi desenvolvido seguindo uma arquitetura de
<strong>monólito modular organizado por domínio</strong>,
priorizando separação de responsabilidades, segurança e escalabilidade.
</p>

</div>


<div class="card">

<h2>🚀 Funcionalidades</h2>

<ul>

<li>
Cadastro e autenticação de usuários utilizando JWT.
</li>

<li>
Verificação de conta através de email.
</li>

<li>
Gerenciamento de perfil de usuário.
</li>

<li>
Registro e gerenciamento de avaliações de filmes e séries.
</li>

<li>
Sistema de favoritos.
</li>

<li>
Lista pessoal de filmes e séries para assistir.
</li>

<li>
Consulta de informações de mídias através da integração com a OMDb API.
</li>

<li>
Controle de permissões entre usuários comuns e administradores.
</li>

<li>
Desativação e gerenciamento administrativo de contas.
</li>

</ul>

</div>



<div class="card">

<h2>🛠 Tecnologias utilizadas</h2>


<span class="badge">Java</span>
<span class="badge">Spring Boot</span>
<span class="badge">Spring Security</span>
<span class="badge">JWT</span>
<span class="badge">Hibernate / JPA</span>
<span class="badge">PostgreSQL</span>
<span class="badge">Flyway</span>
<span class="badge">Docker</span>
<span class="badge">JUnit 5</span>
<span class="badge">Mockito</span>
<span class="badge">OMDb API</span>


</div>



<div class="card">

<h2>🏗 Arquitetura</h2>

<p>
O projeto utiliza uma arquitetura baseada em módulos de domínio,
onde cada contexto possui suas próprias responsabilidades.
</p>


<pre>
com.jpcode.screentrack

├── user
│   ├── User
│   ├── UserService
│   ├── UserController
│   └── DTOs
│
├── auth
│
├── review
│
├── favorite
│
├── watchlist
│
├── media
│
├── admin
│
├── security
│
├── exception
│
└── integration
    └── omdb
</pre>


<p>
Essa organização facilita manutenção, testes e evolução da aplicação.
</p>

</div>



<div class="card">

<h2>🔐 Segurança</h2>

<ul>

<li>
Autenticação baseada em JWT.
</li>

<li>
Criptografia de senhas utilizando BCrypt.
</li>

<li>
Controle de acesso baseado em roles (USER e ADMIN).
</li>

<li>
Dados sensíveis protegidos através de variáveis de ambiente.
</li>

<li>
Configuração separada por ambiente:
development, test e production.
</li>

</ul>

</div>



<div class="card">

<h2>🐳 Executando o projeto</h2>


<h3>Pré-requisitos</h3>

<ul>
<li>Java 25</li>
<li>Maven</li>
<li>Docker</li>
<li>Docker Compose</li>
</ul>


<h3>1. Clone o repositório</h3>


<pre>
git clone https://github.com/JpCode01/Screen-Track.git

cd Screen-Track
</pre>


<h3>2. Configure as variáveis de ambiente</h3>

<p>
Crie um arquivo <code>.env</code> baseado no exemplo fornecido.
</p>


<h3>3. Gere o projeto</h3>

<pre>
mvn clean package
</pre>


<h3>4. Execute com Docker</h3>

<pre>
docker compose up --build
</pre>


<p>
A API estará disponível em:
</p>

<pre>
http://localhost:8080
</pre>


</div>



<div class="card">

<h2>🧪 Testes</h2>

<p>
O projeto possui testes automatizados utilizando:
</p>

<ul>
<li>JUnit 5</li>
<li>Mockito</li>
<li>Spring Boot Test</li>
</ul>

<p>
Os testes validam regras de negócio, serviços,
controllers e comportamento da aplicação.
</p>

</div>



<div class="card">

<h2>📚 Banco de dados</h2>

<p>
A aplicação utiliza PostgreSQL com gerenciamento de migrations através do Flyway.
</p>

<p>
Todas as alterações estruturais do banco são versionadas,
garantindo consistência entre ambientes.
</p>

</div>



<div class="card">


<h2>👨‍💻 Desenvolvedor</h2>

<p>
<strong>JpCode</strong>
</p>

<p>
Projeto desenvolvido para estudo e construção de portfólio
na área de desenvolvimento backend.
</p>

</div>


<footer>
ScreenTrack API © 2026
</footer>


</body>
</html>
