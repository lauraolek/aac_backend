import sys
import ast
from estnltk import Text
from estnltk.vabamorf.morf import synthesize

text = Text("jooma").tag_layer(['morph_analysis'])
#print(text['morph_analysis'][0].annotations[0].get('partofspeech', ''))

def teisenda_ma_tahan_lauseosa(sisend_loend):
    """
    Käänab ja pöörab loendi sõnu vastavalt "Ma tahan" konstruktsiooni reeglitele.
    Kasutab uuemat EstNLTK kättesaamise süntaksit.
    """

    if not sisend_loend:
        return []

    # Eraldame esimese lauseosa, mis peab jääma muutmata
    muutumatu_osa = sisend_loend[0]
    if sisend_loend[0] != "Ma tahan":
        return sisend_loend

    sonad_tootlemiseks = sisend_loend[1:]

    valjund_loend = [muutumatu_osa]

    for sona in sonad_tootlemiseks:
        # Püüame kinni tühjad või liiga lühikesed sõnad või "Ma tahan" kordused
        if not sona or sona.isspace() or sona == "Ma tahan":
            valjund_loend.append(sona)
            continue

        try:
            # 1. Analüüs: Leiame sõnaliigi ja algvormi (lemma)
            text_obj = Text(sona)
            text_obj.tag_layer(['morph_analysis'])

            if not text_obj['morph_analysis']:
                # Kui analüüs ebaõnnestus (nt tundmatu lühend), jätame sõna muutmata
                valjund_loend.append(sona)
                continue

            # Ligipääs analüüsile: EstNLTK teeb automaatselt morfoloogilise analüüsi
            analyys = text_obj['morph_analysis'][0]

            # Kui analüüsi ei leitud, jätame sõna muutmata
            if not analyys.annotations:
                valjund_loend.append(sona)
                continue

            # Võtame esimese (EstNLTK poolt parimaks peetud) analüüsi
            parim_analyys = analyys.annotations[0]
            lemma = parim_analyys.get('lemma')
            sonaliik = parim_analyys.get('partofspeech') # Nt S, A, V, Num

            # Määra soovitud tunnused sõnaliigi järgi
            soovitud_tunnus = None

            if sonaliik in ['S', 'A', 'N', 'Num']: # Nimisõna, Omadussõna, Arvsõna
                soovitud_tunnus = "sg p" # Partitiiv ainsuses
            elif sonaliik == 'V': # Tegusõna
                soovitud_tunnus = "da" # Da-infinitiiv

            if soovitud_tunnus:
                # 2. Generatsioon: Moodustame uue vormi algvormist ja tunnustest
                try:
                    genereeritud_vormid = synthesize(lemma, soovitud_tunnus)

                    if genereeritud_vormid:
                        valjund_loend.append(genereeritud_vormid[0])
                    else:
                        # Kui generatsioon ebaõnnestus, jätame algvormi või algse sõna
                        valjund_loend.append(lemma)
                        #print(f"Hoiatus: Ebaõnnestus generatsioon: {lemma} ({sonaliik}) -> {soovitud_tunnus}")
                except Exception as e:
                    # Jätame sõna muutmata, kui generatsioon viskab vea
                    valjund_loend.append(sona)
                    #print(f"Viga generatsioonil sõnale {sona}: {e}")
            else:
                # Jätame muud sõnad (sidesõnad, määrsõnad jne.) muutmata
                valjund_loend.append(sona)

        except Exception as e:
             # Üldine veapüük analüüsi või Text loomise ajal
             valjund_loend.append(sona)
             #print(f"PYTHON ERROR: Töötlemisel tekkis viga: {e}", file=sys.stderr)

    return valjund_loend


def main():
    try:
        input_data = sys.argv[1] if len(sys.argv) > 1 else ""
        sisend_loend = ast.literal_eval(input_data)

        print(teisenda_ma_tahan_lauseosa(sisend_loend))
    except Exception as e:
        # Prindi viga standardveavoogu (StdErr), et Java saaks selle kinni püüda
        print(f"PYTHON ERROR: Töötlemisel tekkis viga: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()