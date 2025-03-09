import { useState } from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Modal from "@mui/material/Modal";
import styles from "./modal.module.css";
import Radio from "@mui/material/Radio";
import { RadioGroup} from "@mui/material";

const questions = {
    1: ["Distance from city center?"],
    2: ["Building year?"],
    3: ["Balcony availability?"],
    4: ["Having an elevator?"],
    5: ["Nearby public transport?"],
    6: ["Parking space?"]
}

const ModalWindow = ({ isOpen, onClose, data, isLink }) => {
    const [selectedValue, setValue] = useState('r');

    const handleChange = (event) =>{
        const key = event.target.value.split('_')[1];
        setValue(prevState => ({
            ...prevState,
            [key]: event.target.value
        }));
    }

    return (
        <Modal
            open={isOpen}
            onClose={onClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
            className={styles.modalWindow}
        >
            <Box
                sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    width: 400,
                    bgcolor: "background.paper",
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2,
                }}
            >
                <div className={styles.modalBoxDiv}>
                    <Typography id="modal-modal-title" variant="h6" component="h2">
                        Let us know your preferences
                    </Typography>
                    <Typography id="modal-modal-description" sx={{ mt: 2 }}>
                        <ul className={styles.questionsDiv}>
                            <th>
                                <tr style={{display: 'flex', justifyContent: 'space-evenly'}}>
                                    <td>Questions</td>
                                    <td>Importance</td>
                                </tr>
                            </th>
                            <table style={{ width: '100%', tableLayout: 'fixed' }}>
                                <tbody style={{ width: '100%', border: '1px solid red' }}>
                                    {Object.keys(questions).map((key) => {
                                        return (
                                            <tr key={key} className={`row_${key}`} style={{ width: '100%'}}>
                                                <td style={{ display: 'flex', width: '100%', alignItems: 'center' }}>
                                                    <label style={{width: '55%'}}>{questions[key]}</label>
                                                    <RadioGroup 
                                                        className={`radioGroup_${key}`} 
                                                        value={selectedValue[key] || ""} 
                                                        onChange={(e) => handleChange(e)} 
                                                        row 
                                                        style={{
                                                            height: '20px', 
                                                            marginBottom: '15px',
                                                            width: '45%'}}
                                                    >   
                                                        <Radio sx={{ color: 'red', '&.Mui-checked': { color: 'red' }, '& .MuiSvgIcon-root': { fontSize: 20 } }} value={`r_${key}`} />
                                                        <Radio sx={{ color: 'orange', '&.Mui-checked': { color: 'orange' }, '& .MuiSvgIcon-root': { fontSize: 20 } }} value={`y_${key}`} />
                                                        <Radio sx={{ color: 'green', '&.Mui-checked': { color: 'green' }, '& .MuiSvgIcon-root': { fontSize: 20 } }} value={`g_${key}`} />
                                                    </RadioGroup>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </ul>
                    </Typography>
                    <Button onClick={onClose} sx={{ mt: 2 }} variant="contained" className={styles.btn}>Close</Button>
                </div>

            </Box>
        </Modal>
    );
};

export default ModalWindow;