const Footer = () => {
  return (
    <>
      <section class="section bg-gray why">
        <div class="shape2">
          <img src={`${process.env.PUBLIC_URL}/img/shape2.svg`} alt="shapes" />
        </div>
        <div class="container">
          <div class="row justify-content-center">
            <div class="col-md-12 col-lg-12">
              <h2 class="text-center text-black mt-3 mb-4">Why ULCA?</h2>
              <div class="ulcaInfo text-black">
                <ul>
                  <li>
                    Be the premier data repository for Indian language resources
                  </li>
                  <li>
                    Collect datasets for MT, ASR, TTS, OCR and various NLP tasks
                    in standardized but extensible formats
                  </li>
                  <li>
                    Collect extensive metadata related to dataset for various
                    analysis
                  </li>
                  <li>
                    Proper attribution for every contributor at the record level
                  </li>
                  <li>Deduplication capability built-in</li>
                  <li>
                    Simple interface to search and download datasets based on
                    various filters
                  </li>
                  <li>
                    Perform various quality checks on the submitted datasets
                  </li>
                  <li>Trained models for language specific tasks</li>
                  <li>Multiple benchmarks defined for each model task</li>
                  <li>Human validated Benchmark datasets</li>
                  <li>
                    Create and submit new benchmark metrics for any model task
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </section>
      <footer class="footer">
        <div class="container-fluid">
          <div class="footer-links row">
            <div class="col-md-4 footerBg bg1">
              <img
                src={`${process.env.PUBLIC_URL}/img/web.png`}
                alt="Web.png"
                class=""
              />
              <div class="">
                Web
                <br />
                <a
                  class=""
                  href="https://www.meity.gov.in"
                  target="_self"
                  rel="noopener noreferrer"
                >
                  www.meity.gov.in
                </a>
              </div>
            </div>
            <div class="col-md-4 footerBg bg2">
              <img
                src={`${process.env.PUBLIC_URL}/img/email-id.png`}
                alt="email-id.png"
                class=""
              />
              <div class="">
                Mail
                <br />
                <a class="" href="mailto:contact@bhashini.gov.in">
                  contact@bhashini.gov.in
                </a>
              </div>
            </div>
            <div class="col-md-4 footerBg bg3">
              <img
                src={`${process.env.PUBLIC_URL}/img/location.png`}
                alt="location.png"
                class=""
              />
              <div class="">
                Address
                <br />
                <p class="ft-20 fw-500" href="">
                  Electronics Niketan, 6, CGO Complex, Lodhi Road, New Delhi -
                  110003
                </p>
              </div>
            </div>
          </div>
        </div>
        <div class="section primary-color">
          <div class="container">
            <div class="row align-items-center">
              <div class="col-md-6">
                <ul class="d-lg-flex link">
                  <li>
                    <a href="https://bhashini.gov.in/images/Bhashini_-_Whitepaper.pdf">
                      Whitepaper
                    </a>
                  </li>

                  <li>
                    <a href="https://bhashini.gov.in/en/ecosystem">Ecosystem</a>
                  </li>
                  <li>
                    <a href="">
                      <div class="join">
                        <a
                          class="bh-btn-primary"
                          href="http://bhashini.gov.in/bhashadaan"
                          target="_self"
                          rel="noopener noreferrer"
                        >
                          {" "}
                          Join Bhasha Daan{" "}
                        </a>
                      </div>
                    </a>
                  </li>
                </ul>
              </div>
              <div class="col-md-6">
                <ul class="socialLink link d-flex justify-content-end">
                  <li>
                    <a href="https://www.facebook.com/" target="_self">
                      <img
                        src={`${process.env.PUBLIC_URL}/img/facebook.png`}
                        alt="facebook"
                      />
                    </a>
                  </li>
                  <li>
                    <a href="https://twitter.com/" target="_self">
                      <img
                        src={`${process.env.PUBLIC_URL}/img/tw.png`}
                        alt="twitter"
                      />
                    </a>
                  </li>
                  <li>
                    <a href="https://www.instagram.com/" target="_self">
                      {" "}
                      <img
                        src={`${process.env.PUBLIC_URL}/img/insta.png`}
                        alt="instagram"
                      />
                    </a>
                  </li>
                </ul>
              </div>
            </div>
            <hr />
            <div class="row align-items-center mt-4 mb-4">
              <div class="col-md-8 col-lg-6">
                <p class="lighGrey mb-0">
                  Copyright @2021 NLTM. All Rights Reserved.
                  <br /> NLTM: National Language Translation Mission
                </p>
              </div>
              <div class="col-md-4 col-lg-6">
                <ul class="d-lg-flex link justify-content-end ">
                  <li>
                    <a
                      href="https://bhashini.gov.in/en/web-information-manager"
                      target="_self"
                    >
                      Web Information Manager
                    </a>
                  </li>
                  <li>
                    <a
                      href="https://bhashini.gov.in/en/privacy-policy"
                      target="_self"
                    >
                      Privacy Policy
                    </a>
                  </li>
                  <li>
                    <a
                      href="https://bhashini.gov.in/en/terms-conditions"
                      target="_self"
                    >
                      {" "}
                      Terms of Use
                    </a>
                  </li>
                </ul>
              </div>
            </div>
            <div class="row align-items-center justify-content-between mt-5">
              <div class="col-md-3">
                <a href="" class="tdl-logo">
                  <img
                    src={`${process.env.PUBLIC_URL}/img/tdil.png`}
                    alt="TDIL logo"
                  />
                </a>
              </div>
              <div class="col-md-3 text-right">
                <a href="" class="dg-india-logo ">
                  <img
                    src={`${process.env.PUBLIC_URL}/img/dg-india.png`}
                    alt="dg-india logo"
                  />
                </a>
              </div>
            </div>
            <div class="row mt-4 pt-4">
              <div class="col-md-4">
                <p class="lighGrey text-center ft-14">
                  Technology Development for Indian Languages Programme
                </p>
              </div>
              <div class="col-md-4">
                <p class="lighGrey text-center ft-14 ">
                  JavaScript must be enabled to access this site. Supports :
                  Firefox, Google Chrome, Internet Explorer 10.0+, Safari
                </p>
              </div>
              <div class="col-md-4">
                <p class="lighGrey text-center ft-14">
                  Last reviewed and updated on:16–Jun-2021
                </p>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </>
  );
};

export default Footer;
