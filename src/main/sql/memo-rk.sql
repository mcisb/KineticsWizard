--
-- PostgreSQL database dump
--

-- Started on 2008-12-04 17:24:21 GMT

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1838 (class 1262 OID 16395)
-- Name: memo-rk; Type: DATABASE; Schema: -; Owner: -
--

-- CREATE DATABASE "memo-rk" WITH TEMPLATE = template0 ENCODING = 'UTF8';


-- connect "memo-rk"

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 332 (class 2612 OID 16386)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

-- CREATE PROCEDURAL LANGUAGE plpgsql;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1504 (class 1259 OID 16396)
-- Dependencies: 3
-- Name: adm_experiment; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_experiment (
    id character varying(50) NOT NULL,
    name character varying(50),
    set_id character varying(50) NOT NULL,
    start_date date,
    start_time time without time zone,
    end_date date,
    end_time time without time zone,
    comment text
);


--
-- TOC entry 1518 (class 1259 OID 16653)
-- Dependencies: 3
-- Name: adm_experimentparameters; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_experimentparameters (
    id character varying(50) NOT NULL,
    experiment_id character varying(50) NOT NULL,
    name character varying(50),
    value text
);


--
-- TOC entry 1505 (class 1259 OID 16402)
-- Dependencies: 3
-- Name: adm_experimentprotocol; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_experimentprotocol (
    id character varying(50) NOT NULL,
    experiment_type character varying(50) NOT NULL,
    method character varying(50),
    parameters text,
    comment text
);


--
-- TOC entry 1506 (class 1259 OID 16408)
-- Dependencies: 3
-- Name: adm_experimentset; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_experimentset (
    id character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    experiment_type character varying(10) NOT NULL,
    description text,
    lab_id character varying(50) NOT NULL,
    protocol_id character varying(50) NOT NULL,
    conductor_id character varying(50) NOT NULL,
    comment text
);


--
-- TOC entry 1507 (class 1259 OID 16414)
-- Dependencies: 3
-- Name: adm_instrument; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_instrument (
    id character varying(50) NOT NULL,
    name character varying(200) NOT NULL,
    method character varying(50),
    model character varying(200),
    manufacturer character varying(200),
    serial_number character varying(200),
    part_of character varying(50),
    maintenance_history text,
    comment text,
    location character varying(200)
);


--
-- TOC entry 1508 (class 1259 OID 16420)
-- Dependencies: 3
-- Name: adm_instrumentused; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_instrumentused (
    set_id character varying(50) NOT NULL,
    instrument_id character varying(50) NOT NULL
);


--
-- TOC entry 1509 (class 1259 OID 16423)
-- Dependencies: 3
-- Name: adm_lab; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_lab (
    id character varying(50) NOT NULL,
    name character varying(200) NOT NULL,
    address character varying(200),
    phone character varying(50),
    e_mail character varying(50),
    person_id character varying(50),
    comment text
);


--
-- TOC entry 1510 (class 1259 OID 16429)
-- Dependencies: 3
-- Name: adm_person; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_person (
    id character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    affiliation character varying(50) NOT NULL,
    "position" character varying(50),
    address character varying(200),
    phone character varying(50),
    e_mail character varying(50) NOT NULL,
    other text
);


--
-- TOC entry 1511 (class 1259 OID 16435)
-- Dependencies: 3
-- Name: adm_program; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_program (
    id character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    manufacturer character varying(50),
    version character varying(50),
    licence character varying(50),
    user_id character varying(50),
    key_code character varying(50),
    expiry_date date,
    methodology character varying(50) NOT NULL,
    platforms character varying(50),
    files_url character varying(50),
    comment text
);


--
-- TOC entry 1512 (class 1259 OID 16441)
-- Dependencies: 3
-- Name: adm_programused; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE adm_programused (
    experiment_id character varying(50) NOT NULL,
    program_id character varying(50) NOT NULL
);


--
-- TOC entry 1513 (class 1259 OID 16444)
-- Dependencies: 3
-- Name: anl_analysis; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE anl_analysis (
    id character varying(50) NOT NULL,
    experiment_id character varying(50) NOT NULL,
    sample_id character varying(50) NOT NULL,
    name character varying(50),
    software_name character varying(50),
    software_ver character varying(50),
    file_url character varying(100),
    file_type character varying(50),
    comment text
);


--
-- TOC entry 1517 (class 1259 OID 16584)
-- Dependencies: 3
-- Name: anl_timeseries; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE anl_timeseries (
    id character varying(50) NOT NULL,
    analysis_id character varying(50) NOT NULL,
    bigendian boolean NOT NULL,
    encoding_precision integer NOT NULL,
    timepoints text NOT NULL,
    readings text NOT NULL,
    comment text,
    initial_rate double precision
);


--
-- TOC entry 1514 (class 1259 OID 16450)
-- Dependencies: 3
-- Name: smp_sample; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE smp_sample (
    id character varying(50) NOT NULL,
    experiment_id character varying(50) NOT NULL,
    expiry_date date,
    quantity double precision,
    comment text,
    "external_ID" character varying(50),
    bar_code character varying(200)
);


--
-- TOC entry 1515 (class 1259 OID 16456)
-- Dependencies: 3
-- Name: voc_experiment_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE voc_experiment_type (
    entry character varying(50) NOT NULL,
    definition text
);


--
-- TOC entry 1516 (class 1259 OID 16462)
-- Dependencies: 3
-- Name: voc_method; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE voc_method (
    entry character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    definition text
);


--
-- TOC entry 1786 (class 2606 OID 16469)
-- Dependencies: 1504 1504
-- Name: adm_experiment_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_experiment
    ADD CONSTRAINT adm_experiment_pkey PRIMARY KEY (id);


--
-- TOC entry 1814 (class 2606 OID 16657)
-- Dependencies: 1518 1518
-- Name: adm_experimentparameters_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_experimentparameters
    ADD CONSTRAINT adm_experimentparameters_pkey PRIMARY KEY (id);


--
-- TOC entry 1788 (class 2606 OID 16471)
-- Dependencies: 1505 1505
-- Name: adm_experimentprotocol_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_experimentprotocol
    ADD CONSTRAINT adm_experimentprotocol_pkey PRIMARY KEY (id);


--
-- TOC entry 1790 (class 2606 OID 16473)
-- Dependencies: 1506 1506
-- Name: adm_experimentset_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_experimentset
    ADD CONSTRAINT adm_experimentset_pkey PRIMARY KEY (id);


--
-- TOC entry 1792 (class 2606 OID 16475)
-- Dependencies: 1507 1507
-- Name: adm_instrument_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_instrument
    ADD CONSTRAINT adm_instrument_pkey PRIMARY KEY (id);


--
-- TOC entry 1794 (class 2606 OID 16477)
-- Dependencies: 1508 1508 1508
-- Name: adm_instrumentused_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_instrumentused
    ADD CONSTRAINT adm_instrumentused_pkey PRIMARY KEY (set_id, instrument_id);


--
-- TOC entry 1796 (class 2606 OID 16479)
-- Dependencies: 1509 1509
-- Name: adm_lab_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_lab
    ADD CONSTRAINT adm_lab_pkey PRIMARY KEY (id);


--
-- TOC entry 1798 (class 2606 OID 16481)
-- Dependencies: 1510 1510
-- Name: adm_person_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_person
    ADD CONSTRAINT adm_person_pkey PRIMARY KEY (id);


--
-- TOC entry 1800 (class 2606 OID 16483)
-- Dependencies: 1511 1511
-- Name: adm_program_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_program
    ADD CONSTRAINT adm_program_pkey PRIMARY KEY (id);


--
-- TOC entry 1802 (class 2606 OID 16485)
-- Dependencies: 1512 1512 1512
-- Name: adm_programused_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY adm_programused
    ADD CONSTRAINT adm_programused_pkey PRIMARY KEY (experiment_id, program_id);


--
-- TOC entry 1804 (class 2606 OID 16487)
-- Dependencies: 1513 1513
-- Name: anl_analysis_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY anl_analysis
    ADD CONSTRAINT anl_analysis_pkey PRIMARY KEY (id);


--
-- TOC entry 1812 (class 2606 OID 16591)
-- Dependencies: 1517 1517
-- Name: anl_timeseries_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY anl_timeseries
    ADD CONSTRAINT anl_timeseries_pkey PRIMARY KEY (id);


--
-- TOC entry 1806 (class 2606 OID 16489)
-- Dependencies: 1514 1514
-- Name: smp_sample_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY smp_sample
    ADD CONSTRAINT smp_sample_pkey PRIMARY KEY (id);


--
-- TOC entry 1808 (class 2606 OID 16491)
-- Dependencies: 1515 1515
-- Name: voc_experiment_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY voc_experiment_type
    ADD CONSTRAINT voc_experiment_type_pkey PRIMARY KEY (entry);


--
-- TOC entry 1810 (class 2606 OID 16493)
-- Dependencies: 1516 1516
-- Name: voc_method_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY voc_method
    ADD CONSTRAINT voc_method_pkey PRIMARY KEY (entry);


--
-- TOC entry 1815 (class 1259 OID 16663)
-- Dependencies: 1518
-- Name: fki_adm_experiment_id_fkey; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX fki_adm_experiment_id_fkey ON adm_experimentparameters USING btree (experiment_id);


--
-- TOC entry 1835 (class 2606 OID 16658)
-- Dependencies: 1785 1518 1504
-- Name: adm_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_experimentparameters
    ADD CONSTRAINT adm_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES adm_experiment(id);


--
-- TOC entry 1816 (class 2606 OID 16494)
-- Dependencies: 1789 1504 1506
-- Name: adm_experiment_set_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_experiment
    ADD CONSTRAINT adm_experiment_set_id_fkey FOREIGN KEY (set_id) REFERENCES adm_experimentset(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1818 (class 2606 OID 16619)
-- Dependencies: 1515 1807 1505
-- Name: adm_experimentprotocol_experiment_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_experimentprotocol
    ADD CONSTRAINT adm_experimentprotocol_experiment_type_fkey FOREIGN KEY (experiment_type) REFERENCES voc_experiment_type(entry) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1817 (class 2606 OID 16504)
-- Dependencies: 1505 1809 1516
-- Name: adm_experimentprotocol_method_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_experimentprotocol
    ADD CONSTRAINT adm_experimentprotocol_method_fkey FOREIGN KEY (method) REFERENCES voc_method(entry) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1819 (class 2606 OID 16509)
-- Dependencies: 1797 1510 1506
-- Name: adm_experimentset_conductor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_experimentset
    ADD CONSTRAINT adm_experimentset_conductor_id_fkey FOREIGN KEY (conductor_id) REFERENCES adm_person(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1820 (class 2606 OID 16514)
-- Dependencies: 1795 1506 1509
-- Name: adm_experimentset_lab_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_experimentset
    ADD CONSTRAINT adm_experimentset_lab_id_fkey FOREIGN KEY (lab_id) REFERENCES adm_lab(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1821 (class 2606 OID 16519)
-- Dependencies: 1506 1787 1505
-- Name: adm_experimentset_protocol_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_experimentset
    ADD CONSTRAINT adm_experimentset_protocol_id_fkey FOREIGN KEY (protocol_id) REFERENCES adm_experimentprotocol(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1822 (class 2606 OID 16524)
-- Dependencies: 1809 1507 1516
-- Name: adm_instrument_method_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_instrument
    ADD CONSTRAINT adm_instrument_method_fkey FOREIGN KEY (method) REFERENCES voc_method(entry) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1823 (class 2606 OID 16529)
-- Dependencies: 1507 1791 1507
-- Name: adm_instrument_part_of_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_instrument
    ADD CONSTRAINT adm_instrument_part_of_fkey FOREIGN KEY (part_of) REFERENCES adm_instrument(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1824 (class 2606 OID 16534)
-- Dependencies: 1507 1508 1791
-- Name: adm_instrumentused_instrument_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_instrumentused
    ADD CONSTRAINT adm_instrumentused_instrument_id_fkey FOREIGN KEY (instrument_id) REFERENCES adm_instrument(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1825 (class 2606 OID 16539)
-- Dependencies: 1508 1506 1789
-- Name: adm_instrumentused_set_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_instrumentused
    ADD CONSTRAINT adm_instrumentused_set_id_fkey FOREIGN KEY (set_id) REFERENCES adm_experimentset(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1826 (class 2606 OID 16544)
-- Dependencies: 1509 1795 1510
-- Name: adm_person_affiliation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_person
    ADD CONSTRAINT adm_person_affiliation_fkey FOREIGN KEY (affiliation) REFERENCES adm_lab(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1827 (class 2606 OID 16549)
-- Dependencies: 1809 1511 1516
-- Name: adm_program_methodology_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_program
    ADD CONSTRAINT adm_program_methodology_fkey FOREIGN KEY (methodology) REFERENCES voc_method(entry) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1828 (class 2606 OID 16554)
-- Dependencies: 1510 1511 1797
-- Name: adm_program_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_program
    ADD CONSTRAINT adm_program_user_id_fkey FOREIGN KEY (user_id) REFERENCES adm_person(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1829 (class 2606 OID 16559)
-- Dependencies: 1785 1504 1512
-- Name: adm_programused_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_programused
    ADD CONSTRAINT adm_programused_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES adm_experiment(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1830 (class 2606 OID 16564)
-- Dependencies: 1799 1512 1511
-- Name: adm_programused_program_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY adm_programused
    ADD CONSTRAINT adm_programused_program_id_fkey FOREIGN KEY (program_id) REFERENCES adm_program(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1831 (class 2606 OID 16569)
-- Dependencies: 1513 1504 1785
-- Name: anl_analysis_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY anl_analysis
    ADD CONSTRAINT anl_analysis_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES adm_experiment(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1832 (class 2606 OID 16574)
-- Dependencies: 1805 1513 1514
-- Name: anl_analysis_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY anl_analysis
    ADD CONSTRAINT anl_analysis_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES smp_sample(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1834 (class 2606 OID 16592)
-- Dependencies: 1517 1803 1513
-- Name: anl_timeseries_analysis_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY anl_timeseries
    ADD CONSTRAINT anl_timeseries_analysis_id_fkey FOREIGN KEY (analysis_id) REFERENCES anl_analysis(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1833 (class 2606 OID 16579)
-- Dependencies: 1785 1504 1514
-- Name: smp_sample_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY smp_sample
    ADD CONSTRAINT smp_sample_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES adm_experiment(id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 1840 (class 0 OID 0)
-- Dependencies: 3
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2008-12-04 17:24:21 GMT

--
-- PostgreSQL database dump complete
--

